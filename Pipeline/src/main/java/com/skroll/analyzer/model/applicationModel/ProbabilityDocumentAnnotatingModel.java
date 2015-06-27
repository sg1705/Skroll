package com.skroll.analyzer.model.applicationModel;

import com.aliasi.stats.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skroll.analyzer.data.NBMNData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.NBInferenceHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.bn.node.*;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.util.Visualizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentAnnotatingModel extends DocumentAnnotatingModel{

    static final int NUM_ITERATIONS = 5;
    NaiveBayesWithMultiNodes lpnbmModel;

    Document doc;
    // todo: should probably store paragraphs, otherwise, need to recreate it everytime when model has new observations
    List<CoreMap> processedParagraphs = new ArrayList<>();
    NBMNData data;
//    int [][] paraFeatureValsExistAtDocLevel;
//    ProcessedData data;


    // indexed by feature number, paragraph number, category number
    double[][][] messagesToParagraphCategory; //From feature ij to paragraph i category
    // paragraph number, feature number, category number

    double[][][][] messagesToDocumentFeature; //From feature ij to documentFeature j
    // Paragraph number, feature number, category number, false/true

    double[][] paragraphCategoryBelief; // paragraph number, category number
    double[][][] documentFeatureBelief; // feature number, category number, false/true


    public ProbabilityDocumentAnnotatingModel(NaiveBayesWithMultiNodes tnbm, HiddenMarkovModel hmm,
                                              Document doc, ModelRVSetting setting) {
        this(tnbm, hmm, doc, setting.getWordType(), setting.getWordFeatures(), setting.getNbmnConfig());
        modelRVSetting=setting;
    }

    public ProbabilityDocumentAnnotatingModel(NaiveBayesWithMultiNodes tnbm, HiddenMarkovModel hmm,
                                              Document doc,
                                              RandomVariable wordType,
                                              List<RandomVariable> wordFeatures,
                                              NBMNConfig nbmnConfig) {

        super.nbmnConfig = nbmnConfig;
        super.wordType = wordType;
        super.wordFeatures = wordFeatures;

        this.doc = doc;
        this.lpnbmModel = NBInferenceHelper.createLogProbNBMN(tnbm);
        this.hmm = hmm;

        hmm.updateProbabilities();
        this.initialize();
    }

    void initialize(){
//        List<CoreMap> originalParagraphs = doc.getParagraphs();
//        processedParagraphs = DocProcessor.processParas(doc, hmm.size());
        processedParagraphs = DocProcessor.processParas(doc, modelRVSetting.NUM_WORDS_TO_USE_PER_PARAGRAPH);
        data = DocProcessor.getParaDataFromDoc(doc, processedParagraphs, nbmnConfig);
//
//        processedParagraphs = DocProcessor.processParagraphs(originalParagraphs, hmm.size());
//        paraFeatureValsExistAtDocLevel = DocProcessor.getFeaturesVals(
//                nbmnConfig.getFeatureExistsAtDocLevelVarList(),
//                doc.getParagraphs(), processedParagraphs
//        );
        computeInitalBeliefs();
    }

    /**
     * Set belief based on the observed paragraphs.
     * @param observedParagraphs The paragraphs that are observed.
     */

    public void updateBeliefWithObservation(List<CoreMap> observedParagraphs){
        computeInitalBeliefs();
    }

    //todo: should probably set inital belief based on observations if a document is reopened by the trainer or the same user again.
    void computeInitalBeliefs() {

        int[][] allParaFeatures = data.getParaFeatures();
        int[][] allParaDocFeatures = data.getParaDocFeatures();

        List<List<RandomVariable>> docFeatures = nbmnConfig.getDocumentFeatureVarList();
        List<RandomVariable> paraDocFeatures = nbmnConfig.getFeatureExistsAtDocLevelVarList();
        RandomVariable paraCategory = nbmnConfig.getCategoryVar();

        int numParagraphs = processedParagraphs.size();
        // todo: assuming the values are binary sized. need to make this more general.
        messagesToDocumentFeature =
                new double[numParagraphs][docFeatures.size()][getParaCategory().getFeatureSize()][2];
        messagesToParagraphCategory = new double[numParagraphs][paraDocFeatures.size()][2];
        paragraphCategoryBelief = new double[numParagraphs][2];
        documentFeatureBelief = new double[docFeatures.size()][getParaCategory().getFeatureSize()][2];

        // compute initial beliefs
        List<List<DiscreteNode>> docFeatureNodes = lpnbmModel.getDocumentFeatureNodes();
        for (int f=0; f<paraDocFeatures.size(); f++)
            for (int c = 0; c < getParaCategory().getFeatureSize(); c++)
                documentFeatureBelief[f][c] = docFeatureNodes.get(f).get(c).getParameters().clone();

        List<DiscreteNode> fnl = lpnbmModel.getFeatureNodes();
        DiscreteNode categoryNode = lpnbmModel.getCategoryNode();

        // observation is only stored in the orignal paragraphs.
        // May consider to use a separate method to get a list of observed paragrpahs.
        List<CoreMap> originalParagraphs = doc.getParagraphs();
        for (int p = 0; p < processedParagraphs.size(); p++) {
            if (DocProcessor.isParaObserved(originalParagraphs.get(p))) {
                int observedVal = RVValues.getValue(paraCategory, originalParagraphs.get(p));
//                        DocumentAnnotatingHelper.getParagraphFeature(originalParagraphs.get(p), processedParas.get(p), paraCategory);

                for (int i=0; i<paraCategory.getFeatureSize(); i++){
                    if (i==observedVal) paragraphCategoryBelief[p][i] = 0;
                    else paragraphCategoryBelief[p][i] = Double.NEGATIVE_INFINITY;
                }
                continue;
            }

            //todo: consider not using paradoc features here, which should make the code simpler.
//            int[] paraFeatures = ParaProcessor.getFeatureVals(nbmnConfig.getFeatureVarList(),
//                    Arrays.asList(originalParagraphs.get(p), processedParagraphs.get(p)));
            int[] paraFeatures = allParaFeatures[p];
            lpnbmModel.setWordsObservation(ParaProcessor.getWordsList(
                    nbmnConfig.getWordVarList(), processedParagraphs.get(p)));
            lpnbmModel.setParaFeatureObservation(paraFeatures);
//            lpnbmModel.setMultiNodesObservation(allParaDocFeatures[p]);
            paragraphCategoryBelief[p] = categoryNode.getParameters().clone();
            for (int i=0; i<fnl.size(); i++){
                double[] message = NodeInferenceHelper.sumOutOtherNodesWithObservation(fnl.get(i), categoryNode);
                for (int j=0; j<message.length; j++)
                    paragraphCategoryBelief[p][j] += message[j];
            }

            // incorporate word information
            List<WordNode> wordNodes = lpnbmModel.getWordNodes();
            for (WordNode node: wordNodes) {
                double[] message = NodeInferenceHelper.sumOutWordsWithObservation(node);
                for (int j = 0; j < message.length; j++)
                    paragraphCategoryBelief[p][j] += message[j];
            }

            BNInference.normalizeLog(paragraphCategoryBelief[p]);
        }

    }


    void passMessagesToParagraphCategories(){
        List<MultiplexNode> fedna = lpnbmModel.getMultiNodes();

        int[][] paraFeatureValsExistAtDocLevel = data.getParaDocFeatures();
        for (int p=0; p<paragraphCategoryBelief.length; p++){

            lpnbmModel.setMultiNodesObservation(paraFeatureValsExistAtDocLevel[p]);

            for (int f = 0; f < nbmnConfig.getFeatureExistsAtDocLevelVarList().size(); f++) {

                double[][] messageFromDocFeature = new double[documentFeatureBelief[f].length][];
                for (int i = 0; i < messageFromDocFeature.length; i++)
                    messageFromDocFeature[i] = documentFeatureBelief[f][i].clone();

//                for (int i = 0; i < messageFromDocFeature.length; i++) {
                for (int c = 0; c < getParaCategory().getFeatureSize(); c++) { //skip none at index 0
                    for (int b = 0; b <= 1; b++)
                        messageFromDocFeature[c][b] -= messagesToDocumentFeature[p][f][c][b];
//                    }
                }

                messagesToParagraphCategory[p][f] = NodeInferenceHelper.updateMessageToSelectingNode(
                        fedna.get(f), messageFromDocFeature);

                for (int i=0; i<paragraphCategoryBelief[p].length; i++){
                    paragraphCategoryBelief[p][i] += messagesToParagraphCategory[p][f][i];
                }
            }
        }
        for (double[] belief: paragraphCategoryBelief){
            BNInference.normalizeLog(belief);
        }
    }

    void passMessageToDocumentFeatures(){
        int[][] allParaDocFeatures = data.getParaDocFeatures();
        List<List<DiscreteNode>> dfna = lpnbmModel.getDocumentFeatureNodes();
        List<MultiplexNode> multiplexNodes = lpnbmModel.getMultiNodes();
//        LogProbabilityDiscreteNode[] dfna = (LogProbabilityDiscreteNode[]) lpnbmModel.getDocumentFeatureNodeArray();
//        LogProbabilityDiscreteNode[] fedna = (LogProbabilityDiscreteNode[]) lpnbmModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paragraphCategoryBelief.length; p++){

            lpnbmModel.setMultiNodesObservation(allParaDocFeatures[p]);

            for (int f = 0; f < nbmnConfig.getFeatureExistsAtDocLevelVarList().size(); f++) {
                double[] messageFromParaCategory = paragraphCategoryBelief[p].clone();
                for (int i = 0; i < messageFromParaCategory.length; i++)
                    messageFromParaCategory[i] -= messagesToParagraphCategory[p][f][i];
                messagesToDocumentFeature[p][f] = NodeInferenceHelper.updateMessagesFromSelectingNode(
                        multiplexNodes.get(f), messageFromParaCategory);
//                messagesToDocumentFeature[p][f] =  NodeInferenceHelper.sumOutOtherNodesWithObservationAndMessage(
//                        multiplexNodes.get(f), lpnbmModel.getCategoryNode(), messageFromParaCategory, dfna.get(f));
//                        fedna[f].sumOutOtherNodesWithObservationAndMessage(
//                                (LogProbabilityDiscreteNode) lpnbmModel.getCategoryNode(),
//                                messageFromParaCategory, dfna[f]);
                for (int c = 0; c < getParaCategory().getFeatureSize(); c++) {
                    for (int i = 0; i < 2; i++) {
                        documentFeatureBelief[f][c][i] += messagesToDocumentFeature[p][f][c][i];
                    }
                }
            }
        }

        for (double[][] beliefs : documentFeatureBelief) {
            for (double[] belief : beliefs) {
                BNInference.normalizeLog(belief);
            }
        }
    }

    public void updateBeliefs(){
        int numIteration =1;
        for (int i=0; i<numIteration; i++) {
            passMessagesToParagraphCategories();
            passMessageToDocumentFeatures();
        }
        passMessagesToParagraphCategories();
    }





    void normalize(double[] probs){
        double sum=0;
        for (int i=0; i<probs.length; i++) sum+= probs[i];
        for (int i=0; i<probs.length; i++) probs[i] /= sum;
    }

    void normalizeParagraphBelieves(){
        for (int i=0; i<paragraphCategoryBelief.length; i++)
            normalize(paragraphCategoryBelief[i]);
    }

    public void annotateDocument(){


        passMessagesToParagraphCategories();

        for (int i=0; i<NUM_ITERATIONS;i++) {
            passMessageToDocumentFeatures();
            passMessagesToParagraphCategories();
        }
        int numParagraphs = paragraphCategoryBelief.length;

        List<CoreMap> paragraphList = doc.getParagraphs();

        RandomVariable paraCategory = nbmnConfig.getCategoryVar();
        for (int p=0; p<numParagraphs; p++){
            CoreMap paragraph = paragraphList.get(p);
            if (DocProcessor.isParaObserved(paragraph)) continue; // skip observed paragraphs
            RVValues.clearValue(paraCategory, paragraph);
//            DocumentAnnotatingHelper.clearParagraphCateoryAnnotation(paragraph, paraCategory);
            if (paragraph.getTokens().size() == 0)
                continue;
            CoreMap processedPara = processedParagraphs.get(p);

            // using NB category as the prior prob to the input of HMM.
            // This means the HMM output state sequence gives the highest p(HMM observations | given NB observations)
            double[] logPrioProbs =
                    paragraphCategoryBelief[p].clone();

            // can check for NB classification to see if we want to keep checking the words.
            // check here to make it more efficient, or keep going to be more accurate.

            List<Token> tokens = processedPara.getTokens();

            //todo: a hack for TOC annotation. should implement HMM for TOC and annotate base on HMM result
            if ((this.modelRVSetting instanceof TOCModelRVSetting) && logPrioProbs[1] > logPrioProbs[0]) {
                RVValues.addTerms(paraCategory, paragraph, tokens);
                continue;
//                DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, tokens);
            }

            List<String> words = DocumentHelper.getTokenString(tokens);

            String[] wordsArray = words.toArray(new String[words.size()]);

            int length = Math.min(hmm.size(), tokens.size());
            int[][] features = new int[length][wordFeatures.size()];
            for (int i=0; i<length ;i++){
                for (int f=0; f<wordFeatures.size();f++){
//                    features[i][f] = RVValues.getValue(wordFeatures.get(f), tokens.get(i));
//                    features[i][f] = DocumentAnnotatingHelper.getWordFeature(paragraph, tokens.get(i), wordFeatures.get(f));
                    features[i][f] = ParaProcessor.getWordFeatureValue(wordFeatures.get(f),
                            tokens.get(i), Arrays.asList(paragraph, processedPara));
                }
            }
            int[] states = hmm.mostLikelyStateSequence(wordsArray, features, logPrioProbs);

            //assume a definition paragraph always has the first word being a defined term.
            // can do this check after naive bayes to make it faster.
            if (states[0]==0) continue;

            List<Token> terms = new ArrayList<>();

            for (int i=0; i<states.length;i++){
                if (states[i]==1) terms.add(tokens.get(i));
                else {
                    if (terms.size()>0){
                        RVValues.addTerms(paraCategory, paragraph, terms);
//                        DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, terms);
                        terms = new ArrayList<>();
                    }

                }
            }
            if (terms.size()>0){
                RVValues.addTerms(paraCategory, paragraph, terms);
//                DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, terms);
            }

        }

    }

    @JsonIgnore
    public double[][] getParagraphCategoryBelief() {
        return paragraphCategoryBelief;
    }

    public double[][] getParagraphCategoryProbabilities(){
        double[][] paraCatProbs = new double[paragraphCategoryBelief.length][nbmnConfig.getCategoryVar().getFeatureSize()];
        for (int i=0; i<paraCatProbs.length;i++)
            paraCatProbs[i] = paragraphCategoryBelief[i].clone();
        BNInference.convertLogBeliefArrayToProb(paraCatProbs);
        return paraCatProbs;
    }


    public double[][][] getDocumentFeatureProbabilities() {
//        double[][][] docFeatureProbs = new double[documentFeatureBelief.length][getParaCategory().getFeatureSize()-1][2];
//        double[][][] docFeatureProbs = documentFeatureBelief.clone();

        // clone only does a shallow copy. The following nested loop does a deep copy.
        double[][][] docFeatureProbs = new double[documentFeatureBelief.length][][];
        for (int i = 0; i < docFeatureProbs.length; i++) {
            docFeatureProbs[i] = new double[documentFeatureBelief[0].length][];
            for (int j = 0; j < docFeatureProbs[0].length; j++) {
                docFeatureProbs[i][j] = documentFeatureBelief[i][j].clone();
            }
        }


//        for (int i=0; i<documentFeatureBelief.length; i++)
//                docFeatureProbs[i] = documentFeatureBelief[i].clone();
        for (int i=0; i<documentFeatureBelief.length; i++)
            BNInference.convertLogBeliefArrayToProb(docFeatureProbs[i]);
        return docFeatureProbs;
    }

    @JsonIgnore
    public double[][][] getDocumentFeatureBelief() {
        return documentFeatureBelief;
    }

    public NaiveBayesWithMultiNodes getLpnbfModel() {
        return lpnbmModel;
    }

    @Override
    public String toString() {
        int[][] paraFeatureValsExistAtDocLevel = data.getParaDocFeatures();
        return "ProbabilityDocumentAnnotatingModel{" +
                "\nlpnbfModel=\n" + lpnbmModel +
                ", \nparaFeatureValsExistAtDocLevel=\n" + Arrays.deepToString(paraFeatureValsExistAtDocLevel) +
                ", \nparagraphCategoryBelief=\n" + Arrays.deepToString(paragraphCategoryBelief) +
                ", \ndocumentFeatureBelief=\n" + Arrays.deepToString(documentFeatureBelief) +
                '}';
    }

    /**
     * Returns a string representation of the BNI for viewer.
     *
     * @param paraIndex
     * @return
     */
    public HashMap<String, HashMap<String, Double>> toVisualMap(int paraIndex) {
       //covert paraCategoryBelief
        HashMap<String, HashMap<String, Double>> map = new HashMap();
        map.put(this.nbmnConfig.getCategoryVar().getName(),
                Visualizer.toDoubleArrayToMap(this.getParagraphCategoryProbabilities()[paraIndex]));
        for(int ii = 0; ii < documentFeatureBelief.length; ii++) {
            for (int jj = 0; jj < documentFeatureBelief[0].length; jj++) {
                map.put(this.nbmnConfig.getDocumentFeatureVarList().get(ii).get(jj).getName(),
                        Visualizer.toDoubleArrayToMap(this.getDocumentFeatureProbabilities()[ii][jj]));
            }
        }
        return map;
    }

    public List<Double> toParaCategoryDump() {
        //covert paraCategoryBelief
        List<Double> listOfP = new ArrayList();
        for (int ii = 0; ii < this.getParagraphCategoryProbabilities().length; ii++) {
            listOfP.add(this.getParagraphCategoryProbabilities()[ii][1]);
        }
        return listOfP;
    }
}


