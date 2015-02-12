package com.skroll.analyzer.model;

import com.google.common.base.Splitter;
import com.google.common.collect.ObjectArrays;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;


import java.util.*;

/**
 * DefinitionExtractionModel has both a NB and a HMM substructures,
 * and has a link from the class node of NB to the first state of HMM.
 *
 * Created by wei2learn on 1/18/2015.
 */


public class DefinedTermExtractionModel {

    static final int HMM_MODEL_LENGTH = 12;


    HiddenMarkovModel hmm;
    NaiveBayes nb;

    // the link between paragraph category to the state of the first word in the paragraph
    // the links from paragraph category to the remaining states should not be significant,
    //      and makes the model more complicated and expensive.

    static final RandomVariableType[] PARAGRAPH_FEATURES = {
            RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
            //RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
            RandomVariableType.PARAGRAPH_NUMBER_TOKENS};

    //todo: if needed, can add a feature to indicated if a word is used as camel case in the document.
    static final RandomVariableType[] WORD_FEATURES = {
            RandomVariableType.WORD_IN_QUOTES,
            //RandomVariableType.WORD_HAS_SPECIAL_FORMAT,
            //RandomVariableType.WORD_INDEX
    };

    int[][] nbCategoryToHmmState1;

    DefinedTermExtractionModel(){

        int [] paragraphFeatureSizes = new int[PARAGRAPH_FEATURES.length];
        for (int i=0; i<paragraphFeatureSizes.length;i++)
            paragraphFeatureSizes[i] = PARAGRAPH_FEATURES[i].getFeatureSize();
        nb = new NaiveBayes(RandomVariableType.PARAGRAPH_HAS_DEFINITION.getFeatureSize(), paragraphFeatureSizes);

        int []wordFeatureSizes = new int[WORD_FEATURES.length]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  WORD_FEATURES[i].getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
    }

    public void annotateDefinedTermsInParagraph(CoreMap paragraph){
        CoreMap trainingParagraph = DefinedTermExtractionHelper.makeTrainingParagraph(paragraph);
        DataTuple nbDataTuple = DefinedTermExtractionHelper.makeNBDataTuple(trainingParagraph);

        // using NB category as the prior prob to the input of HMM.
        // This means the HMM output state sequence gives the highest p(HMM observations | given NB observations)
        double[] logPrioProbs =
                nb.inferCategoryProbabilitiesMoreStable(nbDataTuple.getTokens(),nbDataTuple.getFeatures());
            //nb.inferLogJointFeaturesProbabilityGivenCategories(nbDataTuple.getTokens(), nbDataTuple.getFeatures());

        // can check for NB classification to see if we want to keep checking the words.
        // check here to make it more efficient, or keep going to be more accurate.

        List<Token> tokens = trainingParagraph.getTokens();
        List<String> words = DocumentHelper.getTokenString(tokens);

        String[] wordsArray = words.toArray(new String[words.size()]);

        int length = Math.min(hmm.size(), tokens.size());
        int[][] features = new int[length][WORD_FEATURES.length];
        for (int i=0; i<length ;i++){
            for (int f=0; f<WORD_FEATURES.length;f++){
                features[i][f] = DefinedTermExtractionHelper.getWordFeature(paragraph, tokens.get(i), WORD_FEATURES[f]);
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

    void updateWithParagraph(CoreMap paragraph) {
        CoreMap trainingParagraph = DefinedTermExtractionHelper.makeTrainingParagraph(paragraph);
        updateNBWithParagraph(trainingParagraph);
        updateHMMWithParagraph(trainingParagraph);
    }

    /**
     * This is required after training the model to convert the frequency counts to probabilities used for inferences.
     */
    void compile(){
        hmm.updateProbabilities();
    }

    void updateNBWithParagraph(CoreMap paragraph){
        DataTuple nbDataTuple = DefinedTermExtractionHelper.makeNBDataTuple(paragraph);
        nb.addSample(nbDataTuple);

    }

    //todo: this method should be changed. Definitions should be annotated with good training data.
    void updateHMMWithParagraph(CoreMap paragraph){
        List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);

        HashSet<String> definitionsSet;
        if (!paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
            definitionsSet= new HashSet<String>();
        } else {
//            List<Token> defTokens = paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class);
//            List<String> definitions = Splitter.on(' ').splitToList(DocumentHelper.getTokenString(defTokens).get(0));
            List<String> definitions = DocumentHelper.getDefinedTerms(paragraph);
            definitionsSet = new HashSet<String>(definitions);
        }

        int[] tokenType = new int[tokens.size()];
        for (int i = 0; i < tokenType.length; i++) {
            tokenType[i] =  DefinedTermExtractionHelper.getWordFeature(
                    paragraph, tokens.get(i), RandomVariableType.WORD_IS_DEFINED_TERM);
        }

        int length = Math.min(hmm.size(), tokens.size());
        int[][] features = new int[length][WORD_FEATURES.length];
        for (int i=0; i<length ;i++){
            for (int f=0; f<WORD_FEATURES.length;f++){
                features[i][f] = DefinedTermExtractionHelper.getWordFeature(paragraph, tokens.get(i), WORD_FEATURES[f]);
            }
        }

        hmm.updateCounts(
                DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
                tokenType, features);

    }

    void updateWithDocument(Document doc){
        List<CoreMap> paragraphs = doc.getParagraphs();

        for( CoreMap paragraph : paragraphs) {
            updateWithParagraph(paragraph);
        }
    }


    void annotateDefinedTermsInDocument(Document doc){
        List<CoreMap> paragraphs = doc.getParagraphs();

        for( CoreMap paragraph : paragraphs) {
            annotateDefinedTermsInParagraph(paragraph);
        }
    }


    @Override
    public String toString() {
        return "DefinedTermExtractionModel{" +
                "hmm=" + hmm +
                ", nb=" + nb +
                ", nbCategoryToHmmState1=" + Arrays.toString(nbCategoryToHmmState1) +
                '}';
    }

}
