package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.LogProbabilityNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.ProbabilityNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.bn.node.LogProbabilityDiscreteNode;
import com.skroll.analyzer.model.bn.node.LogProbabilityWordNode;
import com.skroll.analyzer.model.bn.node.ProbabilityDiscreteNode;
import com.skroll.analyzer.model.bn.node.ProbabilityWordNode;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class ProbabilityDocumentAnnotatingModel extends DocumentAnnotatingModel{

    LogProbabilityNaiveBayesWithFeatureConditions lpnbfModel;

    Document doc;
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
//        int[] wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
//        for (int i = 0; i < wordFeatureSizes.length; i++)
//            wordFeatureSizes[i] = WORD_FEATURES.get(i).getFeatureSize();
//        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
//                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
        initialize();
         initialize(doc);

    }

    void initialize(Document doc){
        List<CoreMap> paragraphs = new ArrayList<>();

        // process raw input paragraph to be used for model
        for( CoreMap paragraph : doc.getParagraphs())
            paragraphs.add(DocumentAnnotatingHelper.processParagraph(paragraph));


        // store feature values for later probability updates
        paraFeatureValsExistAtDocLevel = new int[paragraphs.size()][paraDocFeatures.size()];
        for (int p=0; p<paragraphs.size();p++){
            for (int f=0; f<paraDocFeatures.size(); f++) {
                paraFeatureValsExistAtDocLevel[p][f] = DocumentAnnotatingHelper.getParagraphFeature(
                        paragraphs.get(p), paraDocFeatures.get(f));
            }
        }

        computeInitalBelieves(paragraphs);
    }

    void computeInitalBelieves(List<CoreMap> paragraphs){

        int numParagraphs = paragraphs.size();
        // todo: assuming the values are binary sized. need to make this more general.
        messagesToDocumentFeature = new double[numParagraphs][docFeatures.size()][2];
        messagesToParagraphCategory = new double[numParagraphs][paraDocFeatures.size()][2];
        paragraphCategoryBelief = new double[numParagraphs][2];
        documentFeatureBelief = new double[docFeatures.size()][2];

        // compute initial believes
        LogProbabilityDiscreteNode[] documentFeatureNodeArray =
                (LogProbabilityDiscreteNode[]) lpnbfModel.getDocumentFeatureNodeArray();

//        for (int p=0; p<numParagraphs; p++)
//            for (int f=0; f<DOCUMENT_FEATURES.size(); f++){
//                Arrays.fill(messagesToDocumentFeature[p][f], 1);
//                Arrays.fill(messagesToParagraphCategory[p][f], 1);
//            }
        for (int f=0; f<paraDocFeatures.size(); f++)
            documentFeatureBelief[f] = documentFeatureNodeArray[f].getParameters().clone();

        LogProbabilityDiscreteNode[] fna = (LogProbabilityDiscreteNode[]) lpnbfModel.getFeatureNodeArray();
        LogProbabilityDiscreteNode categoryNode = (LogProbabilityDiscreteNode)lpnbfModel.getCategoryNode();
        for (int p=0; p<paragraphs.size(); p++){
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
                        (LogProbabilityDiscreteNode)lpnbfModel.getCategoryNode(),
                        messageFromParaCategory, dfna[f]);
                for (int i=0; i<documentFeatureBelief[f].length; i++){
                    documentFeatureBelief[f][i] += messagesToDocumentFeature[p][f][i];
                }
            }
        }
    }

    public void updateBelieves(){
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
        int numParagraphs = paragraphCategoryBelief.length;

        List<CoreMap> paragraphList = doc.getParagraphs();

        for (int p=0; p<numParagraphs; p++){
            CoreMap paragraph = paragraphList.get(p);
            if (paragraph.getTokens().size() == 0)
                return;
            CoreMap trainingParagraph = DefinedTermExtractionHelper.makeTrainingParagraph(paragraph);
            DataTuple nbDataTuple = DefinedTermExtractionHelper.makeNBDataTuple(trainingParagraph, paraFeatures);


            // using NB category as the prior prob to the input of HMM.
            // This means the HMM output state sequence gives the highest p(HMM observations | given NB observations)
            double[] logPrioProbs =
                    paragraphCategoryBelief[p];
            //        nb.inferCategoryProbabilitiesMoreStable(nbDataTuple.getTokens(),nbDataTuple.getFeatures());

            //nb.inferLogJointFeaturesProbabilityGivenCategories(nbDataTuple.getTokens(), nbDataTuple.getFeatures());

            // can check for NB classification to see if we want to keep checking the words.
            // check here to make it more efficient, or keep going to be more accurate.

            List<Token> tokens = trainingParagraph.getTokens();
            List<String> words = DocumentHelper.getTokenString(tokens);

            String[] wordsArray = words.toArray(new String[words.size()]);

            int length = Math.min(hmm.size(), tokens.size());
            int[][] features = new int[length][wordFeatures.size()];
            for (int i=0; i<length ;i++){
                for (int f=0; f<wordFeatures.size();f++){
                    features[i][f] = DefinedTermExtractionHelper.getWordFeature(paragraph, tokens.get(i), wordFeatures.get(f));
                }
            }
            int[] states = hmm.mostLikelyStateSequence(wordsArray, features, logPrioProbs);

            //assume a definition paragraph always has the first word being a defined term.
            // can do this check after naive bayes to make it faster.
            if (states[0]==0) return;

            List<Token> definedTerms = new ArrayList<>();

            for (int i=0; i<states.length;i++){
                if (states[i]==1) definedTerms.add(tokens.get(i));
                else {
                    if (definedTerms.size()>0){
                        DocumentHelper.addDefinedTermTokensInParagraph(definedTerms, paragraph);
                        definedTerms = new ArrayList<>();
                    }

                }
            }
            if (definedTerms.size()>0){
                DocumentHelper.addDefinedTermTokensInParagraph(definedTerms, paragraph);
            }
        }

    }

    public double[][] getParagraphCategoryBelief() {
        return paragraphCategoryBelief;
    }

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
}


