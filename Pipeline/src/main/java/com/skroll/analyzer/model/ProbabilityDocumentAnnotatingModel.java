package com.skroll.analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skroll.analyzer.model.bn.LogProbabilityNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.bn.node.LogProbabilityDiscreteNode;
import com.skroll.analyzer.model.bn.node.LogProbabilityWordNode;
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
    LogProbabilityNaiveBayesWithFeatureConditions lpnbfModel;

    Document doc;
    // todo: should probably store paragraphs, otherwise, need to recreate it everytime when model has new observations
    List<CoreMap> processedParagraphs = new ArrayList<>();
    int [][] paraFeatureValsExistAtDocLevel;

    double[][][] messagesToParagraphCategory; //From feature ij to paragraph i category
    double[][][] messagesToDocumentFeature; //From feature ij to documentFeature j
    double[][] paragraphCategoryBelief;
    double[][] documentFeatureBelief;

    public ProbabilityDocumentAnnotatingModel(TrainingNaiveBayesWithFeatureConditions tnbf, HiddenMarkovModel hmm,
                                              Document doc,
                                                RandomVariableType wordType,
                                                List<RandomVariableType> wordFeatures,
                                                RandomVariableType paraCategory,
                                                List<RandomVariableType> paraFeatures,
                                                List<RandomVariableType> paraDocFeatures,
                                                List<RandomVariableType> docFeatures){

            this.wordType = wordType;
            this.wordFeatures = wordFeatures;
            this.paraCategory = paraCategory;
            this.paraFeatures = paraFeatures;
            this.paraDocFeatures = paraDocFeatures;
            this.docFeatures = docFeatures;
        this.doc = doc;
        lpnbfModel = new LogProbabilityNaiveBayesWithFeatureConditions(tnbf);

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

        // store feature values for later probability updates
        paraFeatureValsExistAtDocLevel = new int[processedParagraphs.size()][paraDocFeatures.size()];
        for (int p=0; p< processedParagraphs.size();p++){
            for (int f=0; f<paraDocFeatures.size(); f++) {
                paraFeatureValsExistAtDocLevel[p][f] = DocumentAnnotatingHelper.getParagraphFeature(
                        processedParagraphs.get(p), paraDocFeatures.get(f));
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
    void computeInitalBeliefs(List<CoreMap> paragraphs){

        int numParagraphs = paragraphs.size();
        // todo: assuming the values are binary sized. need to make this more general.
        messagesToDocumentFeature = new double[numParagraphs][docFeatures.size()][2];
        messagesToParagraphCategory = new double[numParagraphs][paraDocFeatures.size()][2];
        paragraphCategoryBelief = new double[numParagraphs][2];
        documentFeatureBelief = new double[docFeatures.size()][2];

        // compute initial beliefs
        LogProbabilityDiscreteNode[] documentFeatureNodeArray =
                (LogProbabilityDiscreteNode[]) lpnbfModel.getDocumentFeatureNodeArray();

        for (int f=0; f<paraDocFeatures.size(); f++)
            documentFeatureBelief[f] = documentFeatureNodeArray[f].getParameters().clone();

        LogProbabilityDiscreteNode[] fna = (LogProbabilityDiscreteNode[]) lpnbfModel.getFeatureNodeArray();
        LogProbabilityDiscreteNode categoryNode = (LogProbabilityDiscreteNode)lpnbfModel.getCategoryNode();

        // observation is only stored in the orignal paragraphs.
        // May consider to use a separate method to get a list of observed paragrpahs.
        List<CoreMap> originalParagraphs = doc.getParagraphs();
        for (int p=0; p<paragraphs.size(); p++){
            if (DocumentAnnotatingHelper.isParaObserved(originalParagraphs.get(p))) {
                int observedVal = DocumentAnnotatingHelper.getParagraphFeature(originalParagraphs.get(p), paraCategory);

                for (int i=0; i<paraCategory.getFeatureSize(); i++){
                    if (i==observedVal) paragraphCategoryBelief[p][i] = 0;
                    else paragraphCategoryBelief[p][i] = Double.NEGATIVE_INFINITY;
                }
                continue;
            }
            SimpleDataTuple tuple = DocumentAnnotatingHelper.makeDataTupleWithOnlyFeaturesObserved(
                    paragraphs.get(p), allParagraphFeatures, docFeatures.size());
            lpnbfModel.setObservation(tuple);
            paragraphCategoryBelief[p] = categoryNode.getLogProbabilities().clone();
            for (int i=0; i<fna.length; i++){
                double[] message = fna[i].sumOutOtherNodesWithObservation( categoryNode);
                for (int j=0; j<message.length; j++)
                    paragraphCategoryBelief[p][j] += message[j];
            }

            // incorporate word information
            LogProbabilityWordNode wordNode = (LogProbabilityWordNode)lpnbfModel.getWordNode();
            double[] message = wordNode.sumOutWordsWithObservation();
            for (int j=0; j<message.length; j++)
                paragraphCategoryBelief[p][j] += message[j];

            BNInference.normalizeLog(paragraphCategoryBelief[p]);
        }

    }


    void passMessagesToParagraphCategories(){
        LogProbabilityDiscreteNode[] dfna = (LogProbabilityDiscreteNode[]) lpnbfModel.getDocumentFeatureNodeArray();
        LogProbabilityDiscreteNode[] fedna = (LogProbabilityDiscreteNode[]) lpnbfModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paragraphCategoryBelief.length; p++){

            lpnbfModel.setObservationOfFeatureNodesExistAtDocLevel(paraFeatureValsExistAtDocLevel[p]);

            for (int f=0; f<paraDocFeatures.size(); f++){
                double[] messageFromDocFeature = documentFeatureBelief[f].clone();
                for (int i=0; i<messageFromDocFeature.length; i++) messageFromDocFeature[i] -= messagesToDocumentFeature[p][f][i];
                messagesToParagraphCategory[p][f] = fedna[f].sumOutOtherNodesWithObservationAndMessage(dfna[f],
                        messageFromDocFeature, (LogProbabilityDiscreteNode)lpnbfModel.getCategoryNode());
                for (int i=0; i<paragraphCategoryBelief[p].length; i++){
                    paragraphCategoryBelief[p][i] += messagesToParagraphCategory[p][f][i];
                }
            }
        }

    }

    void passMessageToDocumentFeatures(){
        LogProbabilityDiscreteNode[] dfna = (LogProbabilityDiscreteNode[]) lpnbfModel.getDocumentFeatureNodeArray();
        LogProbabilityDiscreteNode[] fedna = (LogProbabilityDiscreteNode[]) lpnbfModel.getFeatureExistAtDocLevelArray();

        for (int p=0; p<paragraphCategoryBelief.length; p++){

            lpnbfModel.setObservationOfFeatureNodesExistAtDocLevel(paraFeatureValsExistAtDocLevel[p]);

            for (int f=0; f<paraDocFeatures.size(); f++){
                double[] messageFromParaCategory = paragraphCategoryBelief[p].clone();
                for (int i=0; i<messageFromParaCategory.length; i++) messageFromParaCategory[i] -= messagesToParagraphCategory[p][f][i];
                messagesToDocumentFeature[p][f] = fedna[f].sumOutOtherNodesWithObservationAndMessage(
                        (LogProbabilityDiscreteNode) lpnbfModel.getCategoryNode(),
                        messageFromParaCategory, dfna[f]);
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
        double[][] paraCatProbs = paragraphCategoryBelief.clone();
        BNInference.convertLogBeliefArrayToProb(paraCatProbs);
        return paraCatProbs;
    }


    public double[][] getDocumentFeatureProbabilities(){
        double[][] docFeatureProbs = documentFeatureBelief.clone();
        BNInference.convertLogBeliefArrayToProb(docFeatureProbs);
        return docFeatureProbs;
    }

    @JsonIgnore
    public double[][] getDocumentFeatureBelief() {
        return documentFeatureBelief;
    }

    public LogProbabilityNaiveBayesWithFeatureConditions getLpnbfModel() {
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
        map.put(this.paraCategory.name(), Visualizer.toDoubleArrayToMap(paragraphCategoryBelief[paraIndex]));
        for(int ii = 0; ii < documentFeatureBelief.length; ii++) {
            map.put(this.docFeatures.get(ii).name(), Visualizer.toDoubleArrayToMap(documentFeatureBelief[ii]));
        }
        return map;
    }
}


