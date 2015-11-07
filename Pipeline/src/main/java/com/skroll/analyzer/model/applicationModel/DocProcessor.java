package com.skroll.analyzer.model.applicationModel;

import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.skroll.analyzer.data.NBMNData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by wei on 5/10/15.
 */
public class DocProcessor {
    public static final Logger logger = LoggerFactory.getLogger(DocProcessor.class);

    static int numWordsToUse = ModelRVSetting.NUM_WORDS_TO_USE_PER_PARAGRAPH;

    static LoadingCache<Document, List<CoreMap>> processedParasCache = CacheBuilder.newBuilder()
            .maximumSize(2)
            .build(
                    new CacheLoader<Document, List<CoreMap>>() {
                        @Override
                        public List<CoreMap> load(Document doc) throws Exception {

                            return processParagraphs(doc.getParagraphs(), numWordsToUse);
                        }
                    }
            );

    //ToDO: commented out the cache
    // static Map<Document, List<CoreMap>> processedParasMap = new HashMap<>();
//    static Map<String, NBMNData> processedDataMap = new HashMap<>();
    static Cache<String, NBMNData> processedDataCache = CacheBuilder.newBuilder()
            .maximumSize(2)
            .build();

    public static void setNumWordsToUse(int numWordsToUse) {
        DocProcessor.numWordsToUse = numWordsToUse;
    }

    static List<CoreMap> processParas(Document doc) {

        try {
            return processedParasCache.get(doc);
        } catch (ExecutionException e) {
            e.printStackTrace(System.err);
        }

        return null;

    }


    /**
     * Processes a paragraph by taking the number of starting words to use
     * @param paras
     * @param numWordsToUse
     * @return
     */
    private static List<CoreMap> processParagraphs(List<CoreMap> paras, int numWordsToUse) {
        List<CoreMap> processedParas = new ArrayList<>();
        for (int i = 0; i < paras.size(); i++) {
            processedParas.add(ParaProcessor.processParagraph(paras.get(i), numWordsToUse));
            paras.get(i).set(CoreAnnotations.IndexInteger.class, i);
        }
        return processedParas;
    }

    /**
     * Matches all paragraphs with UserTOC
     *
     * @param paragraphs
     * @param processedParas
     * @return
     */
    static Void annotateProcessParaWithTOCMatch(List<CoreMap> paragraphs, List<CoreMap> processedParas) {
        //create tocTokens
        List<Token> tocTokens = new ArrayList<>();
        List<String> tocParaIds = new ArrayList<>();
        boolean isUserTocPresent = false;
        for(CoreMap p : paragraphs) {
            //get category
            //if (p.containsKey(CoreAnnotations.IsUserDefinedTOCAnnotation.class)) {
            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(p, Category.USER_TOC)) {
                isUserTocPresent = true;
                tocParaIds.add(p.getId());
                List<Token> tokens = p.getTokens().stream()
                        .filter(t -> !(t.getText().equals(".") || t.getText().matches("^[0-9]+$")))
                        .collect(Collectors.toList());
                tocTokens.addAll(tokens);
            }
            //}
        }

        if (!isUserTocPresent)
            return null;

        String tocTokensString = Joiner.on("").join(tocTokens).toLowerCase();
        logger.info("TOC Token Matching String {}", tocTokensString);
        //iterate over each paragraph
        for(int ii = 0; ii < paragraphs.size(); ii++) {
            CoreMap paragraph = paragraphs.get(ii);
            CoreMap processedP = processedParas.get(ii);
            if (tocParaIds.contains(paragraph.getId())) {
                continue;
            }
            List<Token> paragraphTokens = paragraph.getTokens();
            List<Token> filterPTokens = paragraphTokens.stream()
                    .filter(t -> !(t.getText().equals(".") || t.getText().matches("^[0-9]+$")))
                    .collect(Collectors.toList());
            String paraTokenString = Joiner.on("").join(filterPTokens).toLowerCase();
            if (paraTokenString.isEmpty()) {
                continue;
            }
            if (tocTokensString.contains(paraTokenString)) {
                processedP.set(CoreAnnotations.IsInUserDefinedTOCAnnotation.class, true);
                logger.debug("Match {}", paraTokenString);
            } else {
                processedP.set(CoreAnnotations.IsInUserDefinedTOCAnnotation.class, false);
            }
        }

        // todo: in the future, may consider only invalidate the affected data. It is not much different for now.
        processedDataCache.invalidateAll();
        return null;
    }



    public static int[][] getFeaturesVals(List<RandomVariable> rvs,
                                          List<CoreMap> originalParas, List<CoreMap> processedParas) {
        int nP = originalParas.size();
        int[][] features = new int[nP][rvs.size()];
        for (int p = 0; p < nP; p++) {
            List<CoreMap> paras = Arrays.asList(originalParas.get(p), processedParas.get(p));
            for (int f = 0; f < rvs.size(); f++) {
                features[p][f] = ParaProcessor.getFeatureValue(rvs.get(f), paras);
            }
        }
        return features;
    }

    /**
     * for each paragraph, each words random variable, there is a set of words.
     * This method returns the whole collection
     *
     * @param rvs
     * @param processedParas
     * @return
     */
    public static List<String[]>[] getWordsLists(List<RandomVariable> rvs,
                                                 List<CoreMap> processedParas) {
        int nP = processedParas.size();
        List<String[]>[] wordsLists = new ArrayList[nP];
        for (int p = 0; p < nP; p++) {
            wordsLists[p] = ParaProcessor.getWordsList(rvs, processedParas.get(p));
        }
        return wordsLists;

    }


    static boolean isParaObserved(CoreMap para) {
        Boolean isObserved = para.get(CoreAnnotations.IsUserObservationAnnotation.class);
        if (isObserved == null) isObserved = false;
        return isObserved;
    }


    // process the document to make data tuples stored in DocData for models to use
//
//    /**
//     * docFeatures has to be computed for each new set of observed paras,
//     * whereas other feature vals only need to be computed once, so doc features has to be processed separatedly
//     * @param originalParas
//     * @param processedParas
//     * @param config
//     * @return
//     */
//    static DocData getDataFromDoc(List<CoreMap> originalParas, List<CoreMap> processedParas, NBMNConfig config) {
////        DocData data = new DocData(doc, config);
//        DocData data = new DocData();
//        List<RandomVariable> features = config.getAllParagraphFeatures();
////        List<CoreMap> originalParas = doc.getParagraphs();
//        SimpleDataTuple[] tuples = new SimpleDataTuple[originalParas.size()];
//
//        int[] docFeatureVals = generateDocumentFeatures(originalParas, processedParas, config);
//
//        int numVals = features.size() + docFeatureVals.length + 1;
//        for (int p = 0; p < originalParas.size(); p++) {
//            List<CoreMap> paras = Arrays.asList(originalParas.get(p), processedParas.get(p));
//            int[] vals = new int[numVals];
//            int iVal = 0;
//            vals[iVal++] = ParaProcessor.getFeatureValue(config.getCategoryVar(),
//                    Arrays.asList(originalParas.get(p)));
//
//            for (int i = 0; i < features.size(); i++) {
//                vals[iVal++] = ParaProcessor.getFeatureValue(features.get(i), paras);
//            }
//
//            for (int i = 0; i < docFeatureVals.length; i++) vals[iVal++] = docFeatureVals[i];
//
//            List<String[]> wordsList = new ArrayList<>();
//            for (RandomVariable rv : config.getWordVarList()) {
//                wordsList.add(RVValues.getWords(rv, processedParas.get(p)));
//            }
//            tuples[p] = new SimpleDataTuple(wordsList, vals);
//        }
//        data.setDocFeatureValues(docFeatureVals);
//        data.setTuples(tuples);
//        return data;
//    }
//
    static String processedDataCacheKey(Document doc, NBMNConfig config) {

        return doc.getId() + config.getAllParagraphFeatures();
    }

    static NBMNData getParaDataFromDoc(Document doc, NBMNConfig config) {
        String key = processedDataCacheKey(doc, config);
//        NBMNData data = processedDataMap.get(key);
//        if (data != null) return data;
        NBMNData data = null;
        List<CoreMap> processedParas = processParas(doc);
        List<CoreMap> originalParas = doc.getParagraphs();
        try {
            return processedDataCache.get(key, new Callable<NBMNData>() {
                @Override
                public NBMNData call() throws Exception {
                    return getParaDataFromDoc(originalParas, processedParas, config);
                }
            });

        } catch (ExecutionException e) {
            e.printStackTrace(System.err);
        }


//        NBMNData data = getParaDataFromDoc(originalParas, processedParas, config);
        // Wei and Saurabh decided to not cache processedPara because
        // they can change once a user observes UTOC
        //processedDataMap.put(key, data);

        return null;
    }


    private static NBMNData getParaDataFromDoc(List<CoreMap> originalParas, List<CoreMap> processedParas, NBMNConfig config) {
        NBMNData data = new NBMNData();
        data.setParaFeatures(getFeaturesVals(config.getFeatureVarList(), originalParas, processedParas));
        data.setParaDocFeatures(getFeaturesVals(config.getFeatureExistsAtDocLevelVarList(), originalParas, processedParas));
        data.setWordsLists(getWordsLists(config.getWordVarList(), processedParas));

        return data;
    }


    //todo: we're check both processedParagraphs and originalParas. But should probably combine the information and just check one.
    // this method is assuming all the doc features are binary
    // also assumes that originalParas contains index annotation,
    // since observed paragraphs to be processed may not be all the paragraphs in the document.
    public static int[][] generateDocumentFeatures(List<CoreMap> observedParas, int[][] allParaDocFeatures,
                                                   NBMNConfig nbmnConfig) {

        RandomVariable categoryVar = nbmnConfig.getCategoryVar();
        int numCategories = categoryVar.getFeatureSize();
        int[][] docFeatureValues = new int[nbmnConfig.getDocumentFeatureVarList().size()][numCategories];

        for (int[] vals : docFeatureValues)
            Arrays.fill(vals, -1);
        for (int p = 0; p < observedParas.size(); p++) {
            CoreMap paragraph = observedParas.get(p);
            int categoryValue = RVValues.getValue(categoryVar, Arrays.asList(observedParas.get(p)));
            int paraIndex = paragraph.get(CoreAnnotations.IndexInteger.class);
            for (int f = 0; f < docFeatureValues.length; f++) {
                docFeatureValues[f][categoryValue] &= allParaDocFeatures[paraIndex][f];
//                for (int c=0; c<numCategories; c++)
//                    if (RVValues.getValue(categoryVar, observedParas.get(p)) == 1)
//                        docFeatureValues[f][c] &= allParaDocFeatures[paraIndex][f];
            }
        }
        return docFeatureValues;
    }

//    public static int[] generateDocumentFeatures(List<CoreMap> originalParas, List<CoreMap> processedParagraphs,
//                                                 NBMNConfig nbfcConfig) {
//
//        int[] docFeatureValues = new int[nbfcConfig.getDocumentFeatureVarList().size()];
//
//        Arrays.fill(docFeatureValues, 1);
//        for (int p = 0; p < processedParagraphs.size(); p++) {
//            CoreMap paragraph = processedParagraphs.get(p);
//            for (int f = 0; f < docFeatureValues.length; f++) {
//                if (RVValues.getValue(nbfcConfig.getCategoryVar(), originalParas.get(p)) == 1)
//                    docFeatureValues[f] &= (ParaProcessor.getFeatureValue(
//                            nbfcConfig.getFeatureExistsAtDocLevelVarList().get(f),
//                            Arrays.asList(originalParas.get(p), paragraph)));
//            }
//        }
//        return docFeatureValues;
//    }


    /**
     * Remove annotations from paragraphs with duplicate text.
     * @param paragraphs
     * @param categoryId
     */
    public static void removeDuplicateAnnotations(List<CoreMap> paragraphs, int categoryId){

        Set<String> headings = new HashSet<>();
        for (CoreMap para:paragraphs){
            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(para, categoryId)){
                if ( headings.contains( para.getText())) {
                    CategoryAnnotationHelper.clearCategoryAnnotation(para, categoryId);
                } else {
                    headings.add(para.getText());
                }
            }
        }
    }

    public static List<List<List<CoreMap>>> createSections(List<CoreMap> paragraphs,
                                                           List<CoreMap> processedParas,
                                                           RandomVariable paraCategory) {
        final int END = 1;
        final int START = 0;
        List<int[]> sectionIndices = createSections(paragraphs, paraCategory);

        List<List<CoreMap>> sections = new ArrayList<>();
        List<List<CoreMap>> processedSections = new ArrayList<>();
        for (int[] sectionIndex : sectionIndices) {
            List<CoreMap> section = new ArrayList<>();
            List<CoreMap> processedSection = new ArrayList<>();
            for (int i = sectionIndex[START]; i < sectionIndex[END]; i++) {
                section.add(paragraphs.get(i));
                processedSection.add(processedParas.get(i));
            }
            sections.add(section);
            processedSections.add(processedSection);
        }
        return Arrays.asList(sections, processedSections);

    }

    /**
     * returns indices of the paragraphs separated into sections.
     * The returned list contains the start and end indices of the sections
     * Each section is a list of paragraphs.
     *
     * @param paragraphs
     * @param paraCategory   Used to determining the boundary of the sections.
     * @return list of start and endsections indices.
     */
    public static List<int[]> createSections(List<CoreMap> paragraphs,
                                                           RandomVariable paraCategory) {
        int sectionHeading = 1;
        int topHeading = 1;
        int others = 0;
        final int END = 1;
        final int START = 0;

        List<int[]> sections = new ArrayList<>();
        int[] section = null;
        boolean mainBodyStarted = false;
        boolean sectionStarted = false;
        for (int i = 0; i < paragraphs.size(); i++) {
            CoreMap para = paragraphs.get(i);

            int paraClass = RVValues.getValue(paraCategory, Arrays.asList(para));
            if (paraClass == topHeading) mainBodyStarted = true;
            if (!mainBodyStarted) continue;
            if (sectionStarted) {
//                if (paraClass == others) {
//                    section.add(i);
//                } else
                if (paraClass == topHeading || paraClass == sectionHeading) { // end of a section
                    sectionStarted = false;
                    section[END] = i;
                    sections.add(section);
                }
            }
            if (paraClass == sectionHeading) { // start of a section
                sectionStarted = true;
//                section = new ArrayList<>();
                section = new int[2];
                section[START] = i + 1; // start index of the section
            }
        }

        if (sectionStarted) {
            section[END] = paragraphs.size();
            sections.add(section); // add the last section
        }
        return sections;
    }

}
