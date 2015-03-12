package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.ProbabilityNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class TrainingDocumentAnnotatingModel {
    static final int HMM_MODEL_LENGTH = 12;

    TrainingNaiveBayesWithFeatureConditions tnbfModel;
    ProbabilityNaiveBayesWithFeatureConditions pnbfModel;
    HiddenMarkovModel hmm;

    //
//    /** for each document to be analyzed, two arrays of NBs are used
//     *  paragraphClassNBList is to infer paragraph classes
//     *  and documentFeatureNBList is used to infer the hidden document level features
//     *  We do approximate inference that iteratively refine the probabilities of
//     *  paragraph classes and document level features.
//     */
//    List<NaiveBayes> paragraphClassNBList = new ArrayList<>();
//    List<NaiveBayes> documentFeatureNBList = new ArrayList<>();
//
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
    public TrainingDocumentAnnotatingModel(){

        int [] paragraphFeatureSizes = new int[PARAGRAPH_FEATURES.size()];
        for (int i=0; i<paragraphFeatureSizes.length;i++)
            paragraphFeatureSizes[i] = PARAGRAPH_FEATURES.get(i).getFeatureSize();
        tnbfModel = new TrainingNaiveBayesWithFeatureConditions(RandomVariableType.PARAGRAPH_HAS_DEFINITION, PARAGRAPH_FEATURES, DOCUMENT_FEATURES);

        int []wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
    }

    public TrainingDocumentAnnotatingModel(TrainingNaiveBayesWithFeatureConditions tnbfModel){
        this.tnbfModel = tnbfModel;

        int []wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
    }
//
//    /**
//     * use a iterative process to alternatively infer paragraph classes and document features.
//     * @param doc
//     */
//    void analyzeDocument(Document doc){
//        NaiveBayesNew paragraphClassificationNB;
//        NaiveBayesNew documentFeatureNB;
//
//
//    }
//
//    public void annotateDefinedTermsInParagraph(CoreMap paragraph){
//        if (paragraph.getTokens().size()==0) return;
//        CoreMap trainingParagraph = DefinedTermExtractionHelper.makeTrainingParagraph(paragraph);
//        DataTuple nbDataTuple = DefinedTermExtractionHelper.makeNBDataTuple(trainingParagraph, PARAGRAPH_FEATURES);
//
//        //hack. should be removed.
////        if (nb.mostLikelyCategory(nbDataTuple)==1) {
////            paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
////            return;
////        }
////        else if (true) return;
//
//        // using NB category as the prior prob to the input of HMM.
//        // This means the HMM output state sequence gives the highest p(HMM observations | given NB observations)
//        double[] logPrioProbs =
//                nb.inferCategoryProbabilitiesMoreStable(nbDataTuple.getTokens(),nbDataTuple.getFeatures());
//        //nb.inferLogJointFeaturesProbabilityGivenCategories(nbDataTuple.getTokens(), nbDataTuple.getFeatures());
//
//        // can check for NB classification to see if we want to keep checking the words.
//        // check here to make it more efficient, or keep going to be more accurate.
//
//        List<Token> tokens = trainingParagraph.getTokens();
//        List<String> words = DocumentHelper.getTokenString(tokens);
//
//        String[] wordsArray = words.toArray(new String[words.size()]);
//
//        int length = Math.min(hmm.size(), tokens.size());
//        int[][] features = new int[length][WORD_FEATURES.size()];
//        for (int i=0; i<length ;i++){
//            for (int f=0; f<WORD_FEATURES.size();f++){
//                features[i][f] = DefinedTermExtractionHelper.getWordFeature(paragraph, tokens.get(i), WORD_FEATURES.get(f));
//            }
//        }
//        int[] states = hmm.mostLikelyStateSequence(wordsArray, features, logPrioProbs);
//
//        //assume a definition paragraph always has the first word being a defined term.
//        // can do this check after naive bayes to make it faster.
//        if (states[0]==0) return;
//
//        List<Token> definedTerms = new ArrayList<>();
//
//        for (int i=0; i<states.length;i++){
//            if (states[i]==1) definedTerms.add(tokens.get(i));
//            else {
//                if (definedTerms.size()>0){
//                    DocumentHelper.addDefinedTermTokensInParagraph(definedTerms, paragraph);
//                    definedTerms = new ArrayList<>();
//                }
//
//            }
//        }
//        if (definedTerms.size()>0){
//            DocumentHelper.addDefinedTermTokensInParagraph(definedTerms, paragraph);
//        }
//    }
//
    void updateWithParagraph(CoreMap paragraph, int[] docFeatures) {
        CoreMap trainingParagraph = DefinedTermExtractionHelper.makeTrainingParagraph(paragraph);
        updateTNBFWithParagraph(trainingParagraph, docFeatures);
        updateHMMWithParagraph(trainingParagraph);
    }
//
//    /**
//     * This is required after training the model to convert the frequency counts to probabilities used for inferences.
//     */
//    public void compile(){
//        hmm.updateProbabilities();
//    }
//
    void updateTNBFWithParagraph(CoreMap paragraph, int[] docFeatures){
        SimpleDataTuple dataTuple = DocumentAnnotatingHelper.makeDataTuple(paragraph, PARAGRAPH_FEATURES, docFeatures);
        tnbfModel.addSample(dataTuple);
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
        int[][] features = new int[length][WORD_FEATURES.size()];
        for (int i=0; i<length ;i++){
            for (int f=0; f<WORD_FEATURES.size();f++){
                features[i][f] = DefinedTermExtractionHelper.getWordFeature(paragraph, tokens.get(i), WORD_FEATURES.get(f));
            }
        }

        hmm.updateCounts(
                DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
                tokenType, features);

    }
    /**
    * training involves updating Fij for each paragraph i and feature j.
            * @param doc
    */
    public void updateWithDocument(Document doc){



        // determinine the document level features
        // assume all binary
        int[] docFeatures = new int[DOCUMENT_FEATURES.size()];
        Arrays.fill(docFeatures, 1);
        List<CoreMap> paragraphs = doc.getParagraphs();
        for( CoreMap paragraph : paragraphs) {
            for (int f=0; f<docFeatures.length; f++){
                docFeatures[f] *=
                        DefinedTermExtractionHelper.getParagraphFeature(paragraph, PARAGRAPH_FEATURES.get(f));
            }
        }
        for( CoreMap paragraph : paragraphs)
            updateWithParagraph(paragraph, docFeatures);
    }
}


