package com.skroll.analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skroll.analyzer.model.bn.NBFCConfig;
import com.skroll.analyzer.model.bn.NBInferenceHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.bn.node.*;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.util.Visualizer;

import javax.print.Doc;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentAnnotatingModel extends DocumentAnnotatingModel{

    static final int NUM_ITERATIONS = 5;
    NaiveBayesWithFeatureConditions lpnbfModel;

    Document doc;
    // todo: should probably store paragraphs, otherwise, need to recreate it everytime when model has new observations
    List<CoreMap> processedParagraphs = new ArrayList<>();
    int [][] paraFeatureValsExistAtDocLevel;

    double[][][] messagesToParagraphCategory; //From feature ij to paragraph i category
    double[][][] messagesToDocumentFeature; //From feature ij to documentFeature j
    double[][] paragraphCategoryBelief;
    double[][] documentFeatureBelief;

    public ProbabilityDocumentAnnotatingModel(NaiveBayesWithFeatureConditions tnbf, HiddenMarkovModel hmm,
                                              Document doc,
                                                RandomVariableType wordType,
                                                List<RandomVariableType> wordFeatures,
                                                NBFCConfig nbfcConfig){

        this.nbfcConfig = nbfcConfig;
            this.wordType = wordType;
            this.wordFeatures = wordFeatures;

        this.doc = doc;
        lpnbfModel = NBInferenceHelper.createLogProbNBWithFeatureConditions(tnbf);
                //new LogProbabilityNaiveBayesWithFeatureConditions(tnbf);

        this.hmm = hmm;

        hmm.updateProbabilities();
        this.initialize();
         //initialize(doc);

    }

    void initialize(){
        super.initialize();
        List<CoreMap> originalParagraphs = doc.getParagraphs();


        // process raw input paragraph to be used for model
        for( int i=0; i<originalParagraphs.size(); i++ ) {
            CoreMap para = originalParagraphs.get(i);

            // put in paragraph index for easier finding paragraph later
            DocumentAnnotatingHelper.setParagraphFeature(para, RandomVariableType.PARAGRAPH_INDEX, i);
            processedParagraphs.add(DocumentAnnotatingHelper.processParagraph(para, hmm.size()));
        }

        List<RandomVariableType> paraDocFeatures = nbfcConfig.getFeatureExistsAtDocLevelVarList();
        // store feature values for later probability updates
        paraFeatureValsExistAtDocLevel = new int[processedParagraphs.size()][paraDocFeatures.size()];
        for (int p=0; p< processedParagraphs.size();p++){
            for (int f=0; f<paraDocFeatures.size(); f++) {
                paraFeatureValsExistAtDocLevel[p][f] = DocumentAnnotatingHelper.getParagraphFeature(
                        originalParagraphs.get(p), processedParagraphs.get(p), paraDocFeatures.get(f));
            }
        }

        computeInitalBeliefs(processedParagraphs);
    }

    /**
     * Set belief based on the observed paragraphs.
     * @param observedParagraphs The paragraphs that are observed.
     */

    public void updateBeliefWithObservation(List<CoreMap> observedParagraphs){

        computeInitalBeliefs(processedParagraphs);
//        for( CoreMap para : observedParagraphs) {
//            if (para==null) continue;
//            if (!DocumentAnnotatingHelper.isParaObserved(para)) continue;
//            List<Token> tokens = para.getTokens();
//            if (tokens==null || tokens.size()==0) continue;
//            int pIndex = DocumentAnnotatingHelper.getParagraphFeature(para, RandomVariableType.PARAGRAPH_INDEX);
//            int value = DocumentAnnotatingHelper.getParagraphFeature(para, paraCategory);
//
//            for (int i=0; i<paraCategory.getFeatureSize(); i++){
//                if (i==value) paragraphCategoryBelief[pIndex][i] = 0;
//                else paragraphCategoryBelief[pIndex][i] = Double.NEGATIVE_INFINITY;
//            }
//        }

    }

    //todo: should probably set inital belief based on observations if a document is reopened by the trainer or the same user again.
    void computeInitalBeliefs(List<CoreMap> processedParas){

        List<RandomVariableType> docFeatures = nbfcConfig.getDocumentFeatureVarList();
        List<RandomVariableType> paraDocFeatures = nbfcConfig.getFeatureExistsAtDocLevelVarList();
        RandomVariableType paraCategory = nbfcConfig.getCategoryVar();

        int numParagraphs = processedParas.size();
        // todo: assuming the values are binary sized. need to make this more general.
        messagesToDocumentFeature = new double[numParagraphs][docFeatures.size()][2];
        messagesToParagraphCategory = new double[numParagraphs][paraDocFeatures.size()][2];
        paragraphCategoryBelief = new double[numParagraphs][2];
        documentFeatureBelief = new double[docFeatures.size()][2];

        // compute initial beliefs
        List<DiscreteNode> docFeatureNodes = lpnbfModel.getDocumentFeatureNodes();
        for (int f=0; f<paraDocFeatures.size(); f++)
            documentFeatureBelief[f] = docFeatureNodes.get(f).getParameters().clone();

        List<DiscreteNode> fnl = lpnbfModel.getFeatureNodes();
        DiscreteNode categoryNode = lpnbfModel.getCategoryNode();

        // observation is only stored in the orignal paragraphs.
        // May consider to use a separate method to get a list of observed paragrpahs.
        List<CoreMap> originalParagraphs = doc.getParagraphs();
        for (int p=0; p<processedParas.size(); p++){
            if (DocumentAnnotatingHelper.isParaObserved(originalParagraphs.get(p))) {
                int observedVal = DocumentAnnotatingHelper.getParagraphFeature(originalParagraphs.get(p), processedParas.get(p), paraCategory);

                for (int i=0; i<paraCategory.getFeatureSize(); i++){
                    if (i==observedVal) paragraphCategoryBelief[p][i] = 0;
                    else paragraphCategoryBelief[p][i] = Double.NEGATIVE_INFINITY;
                }
                continue;
            }
            SimpleDataTuple tuple = DocumentAnnotatingHelper.makeDataTupleWithOnlyFeaturesObserved(
                    originalParagraphs.get(p), processedParas.get(p), allParagraphFeatures, docFeatures.size(),
                    wordVarList);
            lpnbfModel.setObservation(tuple);
            paragraphCategoryBelief[p] = categoryNode.getParameters().clone();
            for (int i=0; i<fnl.size(); i++){
                double[] message = NodeInferenceHelper.sumOutOtherNodesWithObservation(fnl.get(i), categoryNode);
                        //fnl.get(i).sumOutOtherNodesWithObservation( categoryNode);
                for (int j=0; j<message.length; j++)
                    paragraphCategoryBelief[p][j] += message[j];
            }

            // incorporate word information
            List<WordNode> wordNodes = lpnbfModel.getWordNodes();
            //LogProbabilityWordNode[] wordNodes = (LogProbabilityWordNode[])lpnbfModel.getWordNodeArray();
            for (WordNode node: wordNodes) {
                double[] message = NodeInferenceHelper.sumOutWordsWithObservation(node);
                //node.sumOutWordsWithObservation();
                for (int j = 0; j < message.length; j++)
                    paragraphCategoryBelief[p][j] += message[j];
            }

            BNInference.normalizeLog(paragraphCategoryBelief[p]);
        }

    }


    void passMessagesToParagraphCategories(){
        List<DiscreteNode> dfna = lpnbfModel.getDocumentFeatureNodes();
        List<DiscreteNode> fedna = lpnbfModel.getFeatureExistAtDocLevelNodes();
//        LogProbabilityDiscreteNode[] dfna = (LogProbabilityDiscreteNode[]) lpnbfModel.getDocumentFeatureNodeArray();
//        LogProbabilityDiscreteNode[] fedna = (LogProbabilityDiscreteNode[]) lpnbfModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paragraphCategoryBelief.length; p++){

            lpnbfModel.setObservationOfFeatureNodesExistAtDocLevel(paraFeatureValsExistAtDocLevel[p]);

            for (int f=0; f<nbfcConfig.getFeatureExistsAtDocLevelVarList().size(); f++){
                double[] messageFromDocFeature = documentFeatureBelief[f].clone();
                for (int i=0; i<messageFromDocFeature.length; i++) messageFromDocFeature[i] -= messagesToDocumentFeature[p][f][i];
                messagesToParagraphCategory[p][f] = NodeInferenceHelper.sumOutOtherNodesWithObservationAndMessage(
                        fedna.get(f), dfna.get(f), messageFromDocFeature, lpnbfModel.getCategoryNode());
//                        fedna.get(f).sumOutOtherNodesWithObservationAndMessage(dfna[f],
//                        messageFromDocFeature, (LogProbabilityDiscreteNode)lpnbfModel.getCategoryNode());
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
        List<DiscreteNode> dfna = lpnbfModel.getDocumentFeatureNodes();
        List<DiscreteNode> fedna = lpnbfModel.getFeatureExistAtDocLevelNodes();
//        LogProbabilityDiscreteNode[] dfna = (LogProbabilityDiscreteNode[]) lpnbfModel.getDocumentFeatureNodeArray();
//        LogProbabilityDiscreteNode[] fedna = (LogProbabilityDiscreteNode[]) lpnbfModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paragraphCategoryBelief.length; p++){

            lpnbfModel.setObservationOfFeatureNodesExistAtDocLevel(paraFeatureValsExistAtDocLevel[p]);

            for (int f=0; f<nbfcConfig.getFeatureExistsAtDocLevelVarList().size(); f++){
                double[] messageFromParaCategory = paragraphCategoryBelief[p].clone();
                for (int i=0; i<messageFromParaCategory.length; i++) messageFromParaCategory[i] -= messagesToParagraphCategory[p][f][i];
                messagesToDocumentFeature[p][f] =  NodeInferenceHelper.sumOutOtherNodesWithObservationAndMessage(
                        fedna.get(f), lpnbfModel.getCategoryNode(), messageFromParaCategory, dfna.get(f));
//                        fedna[f].sumOutOtherNodesWithObservationAndMessage(
//                                (LogProbabilityDiscreteNode) lpnbfModel.getCategoryNode(),
//                                messageFromParaCategory, dfna[f]);
                for (int i=0; i<documentFeatureBelief[f].length; i++){
                    documentFeatureBelief[f][i] += messagesToDocumentFeature[p][f][i];
                }
            }
        }
        for (double[] belief: documentFeatureBelief){
            BNInference.normalizeLog(belief);
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

        RandomVariableType paraCategory = nbfcConfig.getCategoryVar();
        for (int p=0; p<numParagraphs; p++){
            CoreMap paragraph = paragraphList.get(p);
            if (DocumentAnnotatingHelper.isParaObserved(paragraph)) continue; // skip observed paragraphs
            DocumentAnnotatingHelper.clearParagraphCateoryAnnotation(paragraph, paraCategory);
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
            if (paraCategory == RandomVariableType.PARAGRAPH_HAS_TOC && logPrioProbs[1]>logPrioProbs[0]) {
                DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, tokens);
                continue;
            }


            List<String> words = DocumentHelper.getTokenString(tokens);

            String[] wordsArray = words.toArray(new String[words.size()]);

            int length = Math.min(hmm.size(), tokens.size());
            int[][] features = new int[length][wordFeatures.size()];
            for (int i=0; i<length ;i++){
                for (int f=0; f<wordFeatures.size();f++){
                    features[i][f] = DocumentAnnotatingHelper.getWordFeature(paragraph, tokens.get(i), wordFeatures.get(f));
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
                        DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, terms);
                        terms = new ArrayList<>();
                    }

                }
            }
            if (terms.size()>0){
                DocumentAnnotatingHelper.addParagraphTermAnnotation(paragraph, paraCategory, terms);
            }

        }

    }

    @JsonIgnore
    public double[][] getParagraphCategoryBelief() {
        return paragraphCategoryBelief;
    }

    public double[][] getParagraphCategoryProbabilities(){
        double[][] paraCatProbs = new double[paragraphCategoryBelief.length][nbfcConfig.getCategoryVar().getFeatureSize()];
        for (int i=0; i<paraCatProbs.length;i++)
            paraCatProbs[i] = paragraphCategoryBelief[i].clone();
        BNInference.convertLogBeliefArrayToProb(paraCatProbs);
        return paraCatProbs;
    }


    public double[][] getDocumentFeatureProbabilities(){
        double[][] docFeatureProbs = new double[documentFeatureBelief.length][2];
        for (int i=0; i<documentFeatureBelief.length; i++)
                docFeatureProbs[i] = documentFeatureBelief[i].clone();
        BNInference.convertLogBeliefArrayToProb(docFeatureProbs);
        return docFeatureProbs;
    }

    @JsonIgnore
    public double[][] getDocumentFeatureBelief() {
        return documentFeatureBelief;
    }

    public NaiveBayesWithFeatureConditions getLpnbfModel() {
        return lpnbfModel;
    }

    @Override
    public String toString() {
        return "ProbabilityDocumentAnnotatingModel{" +
                "\nlpnbfModel=\n" + lpnbfModel +
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
        map.put(this.nbfcConfig.getCategoryVar().name(),
                Visualizer.toDoubleArrayToMap(this.getParagraphCategoryProbabilities()[paraIndex]));
        for(int ii = 0; ii < documentFeatureBelief.length; ii++) {
            map.put(this.nbfcConfig.getDocumentFeatureVarList().get(ii).name(),
                    Visualizer.toDoubleArrayToMap(this.getDocumentFeatureProbabilities()[ii]));
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


