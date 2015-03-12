package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.NaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.ProbabilityNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class DocumentAnnotatingModel {
    static final int HMM_MODEL_LENGTH = 12;

    NaiveBayesWithFeatureConditions nbfModel;
    HiddenMarkovModel hmm;

    static final List<RandomVariableType> DOCUMENT_FEATURES = Arrays.asList(
            RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES
//            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.DOCUMENT_DEFINITIONS_IS_UNDERLINED
    );
    static final List<RandomVariableType> PARAGRAPH_FEATURES = Arrays.asList(
            RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,

            RandomVariableType.PARAGRAPH_NUMBER_TOKENS);

    //todo: if needed, can add a feature to indicated if a word is used as camel case in the document.
    static final List<RandomVariableType> WORD_FEATURES = Arrays.asList(
            RandomVariableType.WORD_IN_QUOTES
//            RandomVariableType.WORD_HAS_SPECIAL_FORMAT,
            //RandomVariableType.WORD_INDEX
    );

    public DocumentAnnotatingModel() {

//        int [] paragraphFeatureSizes = new int[PARAGRAPH_FEATURES.size()];
//        for (int i=0; i<paragraphFeatureSizes.length;i++)
//            paragraphFeatureSizes[i] = PARAGRAPH_FEATURES.get(i).getFeatureSize();
//        nbfModel = new TrainingNaiveBayesWithFeatureConditions(RandomVariableType.PARAGRAPH_HAS_DEFINITION, PARAGRAPH_FEATURES, );
//
//        int []wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
//        for (int i=0; i<wordFeatureSizes.length;i++)
//            wordFeatureSizes[i] =  WORD_FEATURES.get(i).getFeatureSize();
//        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
//                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
    }

    public DocumentAnnotatingModel(TrainingNaiveBayesWithFeatureConditions tnbfModel) {

        this.nbfModel = tnbfModel;

        int[] wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i = 0; i < wordFeatureSizes.length; i++)
            wordFeatureSizes[i] = WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
    }

//    void updateWithParagraph(CoreMap paragraph) {
//        CoreMap trainingParagraph = DefinedTermExtractionHelper.makeTrainingParagraph(paragraph);
//        updateNBWithParagraph(trainingParagraph);
//        updateHMMWithParagraph(trainingParagraph);
//    }
//
//    /**
//     * This is required after training the model to convert the frequency counts to probabilities used for inferences.
//     */
//    public void compile(){
//        hmm.updateProbabilities();
//    }
//
//    void updateNBWithParagraph(CoreMap paragraph){
//        DataTuple nbDataTuple = DefinedTermExtractionHelper.makeNBDataTuple(paragraph, PARAGRAPH_FEATURES);
//        nb.addSample(nbDataTuple);
//
//    }
//
//    //todo: this method should be changed. Definitions should be annotated with good training data.
//    void updateHMMWithParagraph(CoreMap paragraph){
//        List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);
//
//        HashSet<String> definitionsSet;
//        if (!paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
//            definitionsSet= new HashSet<String>();
//        } else {
////            List<Token> defTokens = paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class);
////            List<String> definitions = Splitter.on(' ').splitToList(DocumentHelper.getTokenString(defTokens).get(0));
//            List<String> definitions = DocumentHelper.getDefinedTerms(paragraph);
//            definitionsSet = new HashSet<String>(definitions);
//        }
//
//        int[] tokenType = new int[tokens.size()];
//        for (int i = 0; i < tokenType.length; i++) {
//            tokenType[i] =  DefinedTermExtractionHelper.getWordFeature(
//                    paragraph, tokens.get(i), RandomVariableType.WORD_IS_DEFINED_TERM);
//        }
//
//        int length = Math.min(hmm.size(), tokens.size());
//        int[][] features = new int[length][WORD_FEATURES.size()];
//        for (int i=0; i<length ;i++){
//            for (int f=0; f<WORD_FEATURES.size();f++){
//                features[i][f] = DefinedTermExtractionHelper.getWordFeature(paragraph, tokens.get(i), WORD_FEATURES.get(f));
//            }
//        }
//
//        hmm.updateCounts(
//                DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
//                tokenType, features);
//
//    }
}

