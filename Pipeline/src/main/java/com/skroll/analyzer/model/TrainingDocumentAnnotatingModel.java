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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class TrainingDocumentAnnotatingModel extends DocumentAnnotatingModel{

    TrainingNaiveBayesWithFeatureConditions tnbfModel;

    public TrainingDocumentAnnotatingModel(){
        super();
        //this.doc = doc;

        tnbfModel = new TrainingNaiveBayesWithFeatureConditions(RandomVariableType.PARAGRAPH_HAS_DEFINITION,
                PARAGRAPH_FEATURES, PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL, DOCUMENT_FEATURES);

        int []wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
        //initialize();


    }

    public TrainingDocumentAnnotatingModel(TrainingNaiveBayesWithFeatureConditions tnbfModel){
        super();

        this.tnbfModel = tnbfModel;
        //this.doc = doc;

        int []wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
        //initialize();


    }

    void updateWithParagraph(CoreMap trainingParagraph, int[] docFeatureValues) {
        //CoreMap trainingParagraph = DefinedTermExtractionHelper.makeTrainingParagraph(paragraph);
        updateTNBFWithParagraph(trainingParagraph, docFeatureValues);
        updateHMMWithParagraph(trainingParagraph);
    }

    void updateTNBFWithParagraph(CoreMap paragraph, int[] docFeatureValues){
        SimpleDataTuple dataTuple = DocumentAnnotatingHelper.makeDataTuple(paragraph, allParagraphFeatures, docFeatureValues);
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
     * todo: to reduce memory usage at the cost of more computation, can process training paragraph one by one later instead of process all and store them now
    * training involves updating Fij for each paragraph i and feature j.
    */
    public void updateWithDocument(Document doc){
        List<CoreMap> paragraphs = new ArrayList<>();
        for( CoreMap paragraph : doc.getParagraphs())
            paragraphs.add(DocumentAnnotatingHelper.processParagraph(paragraph));
        int[] docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(paragraphs,DOCUMENT_FEATURES,
                PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL);


        for( CoreMap paragraph : paragraphs)
            updateWithParagraph(paragraph, docFeatureValues);
    }



    @Override
    public String toString() {
        return "TrainingDocumentAnnotatingModel{" +
                "tnbfModel=" + tnbfModel +
                '}';
    }
}


