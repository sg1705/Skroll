package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.data.DocData;
import com.skroll.analyzer.data.NBFCData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;

import javax.print.Doc;
import java.util.*;

/**
 * Created by wei on 5/10/15.
 */
public class DocProcessor {

    static Map<Document, List<CoreMap>> processedDataMap = new HashMap<>();

//    static List<CoreMap> processParagraphs(Document doc, int numWordsToUse) {
//        List<CoreMap> processedParas = processedDataMap.get()
//        List<CoreMap> processedParas = new ArrayList<>();
//        for (int i = 0; i < paras.size(); i++) {
//            processedParas.add(ParaProcessor.processParagraph(paras.get(i), numWordsToUse));
//            paras.get(i).set(CoreAnnotations.IndexInteger.class, i);
//        }
//        return processedParas;
//    }
    /**
     * Processes a paragraph by taking the number of starting words to use
     * @param paras
     * @param numWordsToUse
     * @return
     */
    static List<CoreMap> processParagraphs(List<CoreMap> paras, int numWordsToUse) {
//        List<CoreMap> processedParas = processedDataMap.get()
        List<CoreMap> processedParas = new ArrayList<>();
        for (int i = 0; i < paras.size(); i++) {
            processedParas.add(ParaProcessor.processParagraph(paras.get(i), numWordsToUse));
            paras.get(i).set(CoreAnnotations.IndexInteger.class, i);
        }
        return processedParas;
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
//    static DocData getDataFromDoc(List<CoreMap> originalParas, List<CoreMap> processedParas, NBFCConfig config) {
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


    static NBFCData getParaDataFromDoc(List<CoreMap> originalParas, List<CoreMap> processedParas, NBFCConfig config) {
        NBFCData data = new NBFCData();
        data.setParaFeatures(getFeaturesVals(config.getFeatureVarList(), originalParas, processedParas));
        data.setParaDocFeatures(getFeaturesVals(config.getFeatureExistsAtDocLevelVarList(), originalParas, processedParas));
        data.setWordsLists(getWordsLists(config.getWordVarList(), processedParas));

        return data;
    }


    //todo: we're check both processedParagraphs and originalParas. But should probably combine the information and just check one.
    // this method is assuming all the doc features are binary
    // also assumes that originalParas contains index annotation,
    // since observed paragraphs to be processed may not be all the paragraphs in the document.
    public static int[] generateDocumentFeatures(List<CoreMap> observedParas, int[][] allParaDocFeatures,
                                                 NBFCConfig nbfcConfig) {

        int[] docFeatureValues = new int[nbfcConfig.getDocumentFeatureVarList().size()];

        Arrays.fill(docFeatureValues, 1);
        for (int p = 0; p < observedParas.size(); p++) {
            CoreMap paragraph = observedParas.get(p);
            int paraIndex = paragraph.get(CoreAnnotations.IndexInteger.class);
            for (int f = 0; f < docFeatureValues.length; f++) {
                if (RVValues.getValue(nbfcConfig.getCategoryVar(), observedParas.get(p)) == 1)
                    docFeatureValues[f] &= allParaDocFeatures[paraIndex][f];
            }
        }
        return docFeatureValues;
    }

//    public static int[] generateDocumentFeatures(List<CoreMap> originalParas, List<CoreMap> processedParagraphs,
//                                                 NBFCConfig nbfcConfig) {
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

}
