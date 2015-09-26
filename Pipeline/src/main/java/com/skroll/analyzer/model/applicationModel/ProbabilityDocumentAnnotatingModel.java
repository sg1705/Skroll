package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.NBInferenceHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.*;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentAnnotatingModel extends ProbabilityTextAnnotatingModel {

    static final int NUM_ITERATIONS = 1;
    static final int SECTION_HEADING = 2;
    static final int OTHERS = 0;
    double[] ANNOTATING_THRESHOLD = new double[]{0, .99999, 0.9999};
    Document doc;
    NaiveBayesWithMultiNodes secNbmn = null;
    HiddenMarkovModel secHmm = null;





    public ProbabilityDocumentAnnotatingModel(int id,
                                              NaiveBayesWithMultiNodes tnbm,
                                              HiddenMarkovModel hmm,
                                              NaiveBayesWithMultiNodes secTnbm,
                                              HiddenMarkovModel secHmm,
                                              Document doc, ModelRVSetting setting) {
        this(id,
                tnbm,
                hmm,
                secTnbm,
                secHmm,
                doc,
                setting,
                setting.getWordType(),
                setting.getWordFeatures(),
                setting.getNbmnConfig(),
                setting.getLowLevelNbmnConfig()
        );
    }

    public ProbabilityDocumentAnnotatingModel(int id,
                                              NaiveBayesWithMultiNodes tnbm,
                                              HiddenMarkovModel hmm,
                                              NaiveBayesWithMultiNodes secTnbm,
                                              HiddenMarkovModel secHmm,
                                              Document doc,
                                              ModelRVSetting setting,
                                              RandomVariable wordType,
                                              List<RandomVariable> wordFeatures,
                                              NBMNConfig nbmnConfig,
                                              NBMNConfig secNbmnConfig
    ) {
        super.nbmnConfig = nbmnConfig;
        super.wordType = wordType;
        super.wordFeatures = wordFeatures;
        super.modelRVSetting = setting;
        super.id = id;
        this.doc = doc;
        this.paragraphs = doc.getParagraphs();
        this.nbmnModel = NBInferenceHelper.createLogProbNBMN(tnbm);
        this.hmm = hmm;
        if (secNbmnConfig != null) {
            this.secNbmn = NBInferenceHelper.createLogProbNBMN(secTnbm);
            this.secHmm = secHmm;
        }
        hmm.updateProbabilities();
        secHmm.updateProbabilities();

        preprocessData();
        super.computeInitalBeliefs();
    }

    void preprocessData() {
        processedParagraphs = DocProcessor.processParas(doc);
        modelRVSetting.postProcessFunctions
                .stream()
                .forEach(f -> f.apply(doc.getParagraphs(), processedParagraphs));
        data = DocProcessor.getParaDataFromDoc(doc, nbmnConfig);
    }

//    /**
//     * Set belief based on the observed paragraphs.
//     * @param observedParagraphs The paragraphs that are observed.
//     */
//
//    public void updateBeliefWithObservation(List<CoreMap> observedParagraphs){
//        computeInitalBeliefs();
//    }
//
//    //todo: should probably set inital belief based on observations if a document is reopened by the trainer or the same user again.
//    void computeInitalBeliefs() {
//
//        int[][] allParaFeatures = data.getParaFeatures();
//        int[][] allParaDocFeatures = data.getParaDocFeatures();
//
//        List<List<RandomVariable>> docFeatures = nbmnConfig.getDocumentFeatureVarList();
//        List<RandomVariable> paraDocFeatures = nbmnConfig.getFeatureExistsAtDocLevelVarList();
//        RandomVariable paraCategory = nbmnConfig.getCategoryVar();
//
//        int numParagraphs = processedParagraphs.size();
//        // todo: assuming the values are binary sized. need to make this more general.
//        messagesToDocumentFeature =
//                new double[numParagraphs][docFeatures.size()][getParaCategory().getFeatureSize()][2];
//        messagesToParagraphCategory = new double[numParagraphs][paraDocFeatures.size()][getParaCategory().getFeatureSize()];
//        paragraphCategoryBelief = new double[numParagraphs][getParaCategory().getFeatureSize()];
//        documentFeatureBelief = new double[docFeatures.size()][getParaCategory().getFeatureSize()][2];
//
//        // compute initial beliefs
//        List<List<DiscreteNode>> docFeatureNodes = nbmnModel.getDocumentFeatureNodes();
//        for (int f=0; f<paraDocFeatures.size(); f++)
//            for (int c = 0; c < getParaCategory().getFeatureSize(); c++)
//                documentFeatureBelief[f][c] = docFeatureNodes.get(f).get(c).getParameters().clone();
//
//        List<DiscreteNode> fnl = nbmnModel.getFeatureNodes();
//        DiscreteNode categoryNode = nbmnModel.getCategoryNode();
//
//        // observation is only stored in the orignal paragraphs.
//        // May consider to use a separate method to get a list of observed paragrpahs.
//        List<CoreMap> originalParagraphs = doc.getParagraphs();
//        for (int p = 0; p < processedParagraphs.size(); p++) {
//            if (DocProcessor.isParaObserved(originalParagraphs.get(p))) {
//                int observedVal = RVValues.getValue(paraCategory, originalParagraphs.get(p));
////                        DocumentAnnotatingHelper.getParagraphFeature(originalParagraphs.get(p), processedParas.get(p), paraCategory);
//
//                for (int i=0; i<paraCategory.getFeatureSize(); i++){
//                    if (i==observedVal) paragraphCategoryBelief[p][i] = 0;
//                    else paragraphCategoryBelief[p][i] = Double.NEGATIVE_INFINITY;
//                }
//                continue;
//            }
//
//            //todo: consider not using paradoc features here, which should make the code simpler.
////            int[] paraFeatures = ParaProcessor.getFeatureVals(nbmnConfig.getFeatureVarList(),
////                    Arrays.asList(originalParagraphs.get(p), processedParagraphs.get(p)));
//            int[] paraFeatures = allParaFeatures[p];
//            nbmnModel.setWordsObservation(ParaProcessor.getWordsList(
//                    nbmnConfig.getWordVarList(), processedParagraphs.get(p)));
//            nbmnModel.setParaFeatureObservation(paraFeatures);
////            nbmnModel.setMultiNodesObservation(allParaDocFeatures[p]);
//            paragraphCategoryBelief[p] = categoryNode.getParameters().clone();
//            for (int i=0; i<fnl.size(); i++){
//                if (paraFeatures[i] == -1) continue;
//
//                double[] message = NodeInferenceHelper.sumOutOtherNodesWithObservation(fnl.get(i), categoryNode);
//                for (int j=0; j<message.length; j++)
//                    paragraphCategoryBelief[p][j] += message[j];
//            }
//
//            // incorporate word information
//            List<WordNode> wordNodes = nbmnModel.getWordNodes();
//            for (WordNode node: wordNodes) {
//                double[] message = NodeInferenceHelper.sumOutWordsWithObservation(node);
//                for (int j = 0; j < message.length; j++)
//                    paragraphCategoryBelief[p][j] += message[j];
//            }
//
//            BNInference.normalizeLog(paragraphCategoryBelief[p]);
//        }
//
//    }
//
//
//    void passMessagesToParagraphCategories(){
//        List<MultiplexNode> fedna = nbmnModel.getMultiNodes();
//
//        int[][] paraFeatureValsExistAtDocLevel = data.getParaDocFeatures();
//        for (int p=0; p<paragraphCategoryBelief.length; p++){
//
//            nbmnModel.setMultiNodesObservation(paraFeatureValsExistAtDocLevel[p]);
//
//            for (int f = 0; f < nbmnConfig.getFeatureExistsAtDocLevelVarList().size(); f++) {
//                if (paraFeatureValsExistAtDocLevel[p][f] == -1) continue;
//
//                double[][] messageFromDocFeature = new double[documentFeatureBelief[f].length][];
//                for (int i = 0; i < messageFromDocFeature.length; i++)
//                    messageFromDocFeature[i] = documentFeatureBelief[f][i].clone();
//
//
////                for (int i = 0; i < messageFromDocFeature.length; i++) {
//                for (int c = 0; c < getParaCategory().getFeatureSize(); c++) { //skip none at index 0
//
//                    // should normalize messageFromDocFeature here if we normalize documentFeatureBelief.
////                    BNInference.normalizeLog(messagesToDocumentFeature[p][f][c]);
//
//                    for (int b = 0; b <= 1; b++)
//                        messageFromDocFeature[c][b] -= messagesToDocumentFeature[p][f][c][b];
////                    }
//                }
//
//
////                BNInference.normalizeLog(messageFromDocFeature); //normalize to prevent overflow
////                // This normalization is independent of the normalization for other paras,
////                // which should be fine as long as the message contributions are normalized fairly for the para.
//
//                for (double[] message : messageFromDocFeature) {
//                    BNInference.normalizeLogProb(message);
//                }
//
//                messagesToParagraphCategory[p][f] = NodeInferenceHelper.updateMessageToSelectingNode(
//                        fedna.get(f), messageFromDocFeature);
//
////                BNInference.normalizeLog(messagesToParagraphCategory[p][f]); //normalize to prevent overflow
//                // This normalization is independent of the normalization for other paras,
//                // which should be fine as long as the message contributions are normalized fairly for the para.
//
//                for (int i=0; i<paragraphCategoryBelief[p].length; i++){
//                    paragraphCategoryBelief[p][i] += messagesToParagraphCategory[p][f][i];
//                }
//
////                BNInference.normalizeLog(messagesToParagraphCategory[p][f]);
//            }
//        }
//
////        BNInference.normalizeLog(paragraphCategoryBelief);
//        for (double[] belief: paragraphCategoryBelief){
//            BNInference.normalizeLog(belief);
//        }
//
//    }
//
//    void passMessageToDocumentFeatures(){
//        int[][] allParaDocFeatures = data.getParaDocFeatures();
//        List<List<DiscreteNode>> dfna = nbmnModel.getDocumentFeatureNodes();
//        List<MultiplexNode> multiplexNodes = nbmnModel.getMultiNodes();
////        LogProbabilityDiscreteNode[] dfna = (LogProbabilityDiscreteNode[]) nbmnModel.getDocumentFeatureNodeArray();
////        LogProbabilityDiscreteNode[] fedna = (LogProbabilityDiscreteNode[]) nbmnModel.getFeatureExistAtDocLevelArray();
//
//        // normalize to prevent overflow
//        // normalize here instead of at the end of the method, since messageToDocumentFeature is linked with documentFeatureBelief
//        //  and is used in the method passMessageToParagraphCategories next. If documentFeatureBelief is normalized
//        //  at the end of the method, the probabilities would become inconsistent and cause incorrect calculations.
//        for (double[][] beliefs : documentFeatureBelief) {
//            for (double[] belief : beliefs) {
//                BNInference.normalizeLog(belief);
//            }
//        }
//
//
//        for (int p=0; p<paragraphCategoryBelief.length; p++){
//
//            nbmnModel.setMultiNodesObservation(allParaDocFeatures[p]);
//
//            for (int f = 0; f < nbmnConfig.getFeatureExistsAtDocLevelVarList().size(); f++) {
//                if (allParaDocFeatures[p][f] == -1) continue;
//                double[] messageFromParaCategory = paragraphCategoryBelief[p].clone();
//                for (int i = 0; i < messageFromParaCategory.length; i++)
//                    messageFromParaCategory[i] -= messagesToParagraphCategory[p][f][i];
//
////                BNInference.normalizeLog(messageFromParaCategory); //normalize to prevent overflow
//                messagesToDocumentFeature[p][f] = NodeInferenceHelper.updateMessagesFromSelectingNode(
//                        multiplexNodes.get(f), messageFromParaCategory);
////                BNInference.normalizeLog(messagesToDocumentFeature[p][f]); //normalize to prevent overflow
//
////                messagesToDocumentFeature[p][f] =  NodeInferenceHelper.sumOutOtherNodesWithObservationAndMessage(
////                        multiplexNodes.get(f), nbmnModel.getCategoryNode(), messageFromParaCategory, dfna.get(f));
////                        fedna[f].sumOutOtherNodesWithObservationAndMessage(
////                                (LogProbabilityDiscreteNode) nbmnModel.getCategoryNode(),
////                                messageFromParaCategory, dfna[f]);
//                for (int c = 0; c < getParaCategory().getFeatureSize(); c++) {
//                    for (int i = 0; i < 2; i++) {
//                        documentFeatureBelief[f][c][i] += messagesToDocumentFeature[p][f][c][i];
//                    }
//                }
//            }
//        }
//
////        BNInference.normalizeLog(documentFeatureBelief);
//
////        for (double[][] beliefs : documentFeatureBelief) {
////            for (double[] belief : beliefs) {
////                BNInference.normalizeLog(belief);
////            }
////        }
//    }
//
//    public void updateBeliefs(){
//        int numIteration =1;
//        for (int i=0; i<numIteration; i++) {
//            passMessagesToParagraphCategories();
//            passMessageToDocumentFeatures();
//        }
//        passMessagesToParagraphCategories();
//    }
//
//
//
//
//
//    void normalize(double[] probs){
//        double sum=0;
//        for (int i=0; i<probs.length; i++) sum+= probs[i];
//        for (int i=0; i<probs.length; i++) probs[i] /= sum;
//    }
//
//    void normalizeParagraphBelieves(){
//        for (int i=0; i<paragraphCategoryBelief.length; i++)
//            normalize(paragraphCategoryBelief[i]);
//    }
//


    @Override
    public void annotateParagraphs() {

        super.annotateParagraphs();
        List<List<List<CoreMap>>> sectionsList = DocProcessor.createSections(paragraphs, processedParagraphs, getParaCategory());
        List<List<CoreMap>> sections = sectionsList.get(0);
        List<List<CoreMap>> processedSections = sectionsList.get(1);

        ModelRVSetting lowerTOCSetting = new TOCModelRVSetting(ClassifierFactory.LOWER_TOC_CATEGORY_IDS, null);
        for (int i = 0; i < sections.size(); i++) {
            ProbabilityTextAnnotatingModel secModel = new ProbabilityTextAnnotatingModel(
                    secNbmn,
                    secHmm,
                    sections.get(i),
                    processedSections.get(i),
                    data,
                    lowerTOCSetting,
                    wordType,
                    wordFeatures,
                    lowerTOCSetting.getNbmnConfig()
            );
            secModel.annotateParagraphs();
        }
//
//        passMessagesToParagraphCategories();
//
//        for (int i=0; i<NUM_ITERATIONS;i++) {
//            passMessageToDocumentFeatures();
//            passMessagesToParagraphCategories();
//        }
//        int numParagraphs = paragraphCategoryBelief.length;
//
//        List<CoreMap> paragraphList = doc.getParagraphs();
//
//        RandomVariable paraCategory = nbmnConfig.getCategoryVar();
//        for (int p=0; p<numParagraphs; p++){
//            CoreMap paragraph = paragraphList.get(p);
//            if (DocProcessor.isParaObserved(paragraph)) continue; // skip observed paragraphs
//            RVValues.clearValue(paraCategory, paragraph);
////            DocumentAnnotatingHelper.clearParagraphCateoryAnnotation(paragraph, paraCategory);
//            if (paragraph.getTokens().size() == 0)
//                continue;
//            CoreMap processedPara = processedParagraphs.get(p);
//
//            // using NB category as the prior prob to the input of HMM.
//            // This means the HMM output state sequence gives the highest p(HMM observations | given NB observations)
//            double[] logPrioProbs =
//                    paragraphCategoryBelief[p].clone();
//
//            // can check for NB classification to see if we want to keep checking the words.
//            // check here to make it more efficient, or keep going to be more accurate.
//
//            List<Token> tokens = processedPara.getTokens();
//
//            //todo: a hack for TOC and DocType annotation. should implement HMM for TOC and annotate base on HMM result
//            if (this.modelRVSetting instanceof TOCModelRVSetting || this.modelRVSetting instanceof DocTypeModelRVSetting ) {
//                int maxIndex = BNInference.maxIndex(logPrioProbs);
//                double[] paraProbs = logPrioProbs.clone();
//                BNInference.convertLogBeliefToProb(paraProbs);
//                if (paraProbs[maxIndex] > ANNOTATING_THRESHOLD[maxIndex])
//                    RVValues.addTerms(paraCategory, paragraph, tokens, maxIndex);
//                if (true) continue;
//            }
//
//            List<String> words = DocumentHelper.getTokenString(tokens);
//
//            String[] wordsArray = words.toArray(new String[words.size()]);
//
//            int length = Math.min(hmm.size(), tokens.size());
//            int[][] features = new int[length][wordFeatures.size()];
//            for (int i=0; i<length ;i++){
//                for (int f=0; f<wordFeatures.size();f++){
////                    features[i][f] = RVValues.getValue(wordFeatures.get(f), tokens.get(i));
////                    features[i][f] = DocumentAnnotatingHelper.getWordFeature(paragraph, tokens.get(i), wordFeatures.get(f));
//                    features[i][f] = ParaProcessor.getWordFeatureValue(wordFeatures.get(f),
//                            tokens.get(i), Arrays.asList(paragraph, processedPara));
//                }
//            }
//            int[] states = hmm.mostLikelyStateSequence(wordsArray, features, logPrioProbs);
//
//            //assume a definition paragraph always has the first word being a defined term.
//            // can do this check after naive bayes to make it faster.
//            if (states[0]==0) continue;
//
//            List<Token> terms = new ArrayList<>();
//
//            for (int i=0; i<states.length;i++){
//                if (states[i]==1) terms.add(tokens.get(i));
//                else {
//                    if (terms.size()>0){
//                        RVValues.addTerms(paraCategory, paragraph, terms, 1);
////                        DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, terms);
//                        terms = new ArrayList<>();
//                    }
//
//                }
//            }
//            if (terms.size()>0){
//                RVValues.addTerms(paraCategory, paragraph, terms, 1);
////                DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, terms);
//            }
//
//        }

    }

//    @JsonIgnore
//    public double[][] getParagraphCategoryBelief() {
//        return paragraphCategoryBelief;
//    }
//
//    public double[][] getParagraphCategoryProbabilities(){
//        double[][] paraCatProbs = new double[paragraphCategoryBelief.length][nbmnConfig.getCategoryVar().getFeatureSize()];
//        for (int i=0; i<paraCatProbs.length;i++)
//            paraCatProbs[i] = paragraphCategoryBelief[i].clone();
//        BNInference.convertLogBeliefArrayToProb(paraCatProbs);
//        return paraCatProbs;
//    }
//
//
//    public double[][][] getDocumentFeatureProbabilities() {
////        double[][][] docFeatureProbs = new double[documentFeatureBelief.length][getParaCategory().getFeatureSize()-1][2];
////        double[][][] docFeatureProbs = documentFeatureBelief.clone();
//
//        // clone only does a shallow copy. The following nested loop does a deep copy.
//        double[][][] docFeatureProbs = new double[documentFeatureBelief.length][][];
//        for (int i = 0; i < docFeatureProbs.length; i++) {
//            docFeatureProbs[i] = new double[documentFeatureBelief[0].length][];
//            for (int j = 0; j < docFeatureProbs[0].length; j++) {
//                docFeatureProbs[i][j] = documentFeatureBelief[i][j].clone();
//            }
//        }
//
//
////        for (int i=0; i<documentFeatureBelief.length; i++)
////                docFeatureProbs[i] = documentFeatureBelief[i].clone();
//        for (int i=0; i<documentFeatureBelief.length; i++)
//            BNInference.convertLogBeliefArrayToProb(docFeatureProbs[i]);
//        return docFeatureProbs;
//    }
//
//    @JsonIgnore
//    public double[][][] getDocumentFeatureBelief() {
//        return documentFeatureBelief;
//    }
//
//    public NaiveBayesWithMultiNodes getLpnbfModel() {
//        return nbmnModel;
//    }
//
//    @Override
//    public String toString() {
//        int[][] paraFeatureValsExistAtDocLevel = data.getParaDocFeatures();
//        return "ProbabilityDocumentAnnotatingModel{" +
//                "\nlpnbfModel=\n" + nbmnModel +
//                ", \nparaFeatureValsExistAtDocLevel=\n" + Arrays.deepToString(paraFeatureValsExistAtDocLevel) +
//                ", \nparagraphCategoryBelief=\n" + Arrays.deepToString(paragraphCategoryBelief) +
//                ", \ndocumentFeatureBelief=\n" + Arrays.deepToString(documentFeatureBelief) +
//                '}';
//    }
//
//    /**
//     * Returns a string representation of the BNI for viewer.
//     *
//     * @param paraIndex
//     * @return
//     */
//    public HashMap<String, HashMap<String, HashMap<String, Double>>> toVisualMap(int paraIndex) {
//       //covert paraCategoryBelief
//        HashMap<String, HashMap<String, HashMap<String, Double>>> map = new LinkedHashMap();
//        HashMap<String, HashMap<String, Double>> applicationModelInfo = new LinkedHashMap();
//        applicationModelInfo.put(this.nbmnConfig.getCategoryVar().getName(),
//                Visualizer.toDoubleArrayToMap(this.getParagraphCategoryProbabilities()[paraIndex]));
//        for (int ii = 0; ii < documentFeatureBelief.length; ii++) {
//            for (int jj = 0; jj < documentFeatureBelief[0].length; jj++) {
//                applicationModelInfo.put(this.nbmnConfig.getDocumentFeatureVarList().get(ii).get(jj).getName(),
//                        Visualizer.toDoubleArrayToMap(this.getDocumentFeatureProbabilities()[ii][jj]));
//            }
//        }
//
//        map.put("applicationModelInfo", applicationModelInfo);
//        return super.toVisualMap(map);
//
//    }
//
//    public List<Double> toParaCategoryDump() {
//        List<AbstractMap.SimpleImmutableEntry<Integer, Double>> probs = new LinkedList<>();
//
//        //covert paraCategoryBelief
//        List<Double> listOfP = new ArrayList();
//        for (int ii = 0; ii < this.getParagraphCategoryProbabilities().length; ii++) {
//            listOfP.add(this.getParagraphCategoryProbabilities()[ii][1]);
//        }
//        return listOfP;
//    }
//
//    public Map<String, Double> getParaProbMap() {
//        Map<String, Double> paraProbMap = new HashMap<>();
//        double[][] probs = this.getParagraphCategoryProbabilities();
//        List<CoreMap> originalParagraphs = doc.getParagraphs();
//
//        for (int ii = 0; ii < probs.length; ii++) {
//            int maxIndex = BNInference.maxIndex(probs[ii]);
//            paraProbMap.put(originalParagraphs.get(ii).getId(), probs[ii][maxIndex]);
//        }
//
//        return paraProbMap;
//    }

}


