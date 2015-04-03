package com.skroll.analyzer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.bn.node.TrainingDiscreteNode;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.util.Visualizer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class TrainingDocumentAnnotatingModel extends DocumentAnnotatingModel{

    TrainingNaiveBayesWithFeatureConditions tnbfModel;

    public TrainingDocumentAnnotatingModel(){
        this(DEFAULT_WORD_TYPE, DEFAULT_WORD_FEATURES,
                DEFAULT_PARAGRAPH_CATEGORY, DEFAULT_PARAGRAPH_FEATURES,
                DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL, DEFAULT_DOCUMENT_FEATURES);
    }

    public TrainingDocumentAnnotatingModel(RandomVariableType wordType,
                                           List<RandomVariableType> wordFeatures,
                                           RandomVariableType paraCategory,
                                           List<RandomVariableType> paraFeatures,
                                           List<RandomVariableType> paraDocFeatures,
                                           List<RandomVariableType> docFeatures){

        this(new TrainingNaiveBayesWithFeatureConditions(paraCategory,
                        paraFeatures, paraDocFeatures, docFeatures),
                wordType, wordFeatures, paraCategory, paraFeatures, paraDocFeatures, docFeatures);

    }

    @JsonCreator
    public TrainingDocumentAnnotatingModel(
            @JsonProperty("tnbfModel")TrainingNaiveBayesWithFeatureConditions tnbfModel,
            @JsonProperty("wordType")RandomVariableType wordType,
            @JsonProperty("wordFeatures")List<RandomVariableType> wordFeatures,
            @JsonProperty("paraCategory")RandomVariableType paraCategory,
            @JsonProperty("paraFeatures")List<RandomVariableType> paraFeatures,
            @JsonProperty("paraDocFeatures")List<RandomVariableType> paraDocFeatures,
            @JsonProperty("docFeatures")List<RandomVariableType> docFeatures){

        this.tnbfModel = tnbfModel;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.paraCategory = paraCategory;
        this.paraFeatures = paraFeatures;
        this.paraDocFeatures = paraDocFeatures;
        this.docFeatures = docFeatures;

        int []wordFeatureSizes = new int[wordFeatures.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  wordFeatures.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                wordType.getFeatureSize(), wordFeatureSizes);
        initialize();


    }

    void updateWithParagraph(CoreMap trainingParagraph, int[] docFeatureValues) {
        updateTNBFWithParagraph(trainingParagraph, docFeatureValues);
       updateHMMWithParagraph(trainingParagraph);
    }

    void updateWithParagraphWithWeights(CoreMap trainingParagraph, CoreMap originalParagraph, int[] docFeatureValues) {
        double[] weights = getTrainingWeights(originalParagraph);
        updateTNBFWithParagraphAndWeight(trainingParagraph, docFeatureValues, weights);
        //todo: consider doing weight for HMM
        updateHMMWithParagraph(trainingParagraph);
        //updateHMMWithParagraphAndWeight(trainingParagraph,weights);
    }

    void updateTNBFWithParagraph(CoreMap paragraph, int[] docFeatureValues){
        SimpleDataTuple dataTuple = DocumentAnnotatingHelper.makeDataTuple(paragraph, paraCategory, allParagraphFeatures, docFeatureValues);
        tnbfModel.addSample(dataTuple);
    }

    double[] getTrainingWeights(CoreMap para){
        double[][] weights = TrainingWeightAnnotationHelper.getParagraphWeight(para, paraCategory);
        double[] oldWeights =weights[0];
        double[] newWeights = weights[1];
        double[] normalizedOldWeights = BNInference.normalize(oldWeights, 1);
        double[] normalizedNewWeights = BNInference.normalize(newWeights, 1);
        double[] trainingWeights = normalizedNewWeights;
        for (int i=0; i<trainingWeights.length; i++)
            trainingWeights[i] -= normalizedOldWeights[i];

        return trainingWeights;
    }

    //todo: need to use the right annotation for the weight.
    void updateTNBFWithParagraphAndWeight(CoreMap paragraph, int[] docFeatureValues, double[] weights) {
        SimpleDataTuple dataTuple = DocumentAnnotatingHelper.makeDataTuple(paragraph, paraCategory, allParagraphFeatures, docFeatureValues);
        String[] words = dataTuple.getWords();
        int[] values = dataTuple.getDiscreteValues();
        int numCategories = paraCategory.getFeatureSize();


        for (int i = 0; i < numCategories; i++) {
            values[0] = i;
            tnbfModel.addSample(dataTuple, weights[i]);
        }
    }
    //todo: this method should be changed. Definitions should be annotated with good training data.
    void updateHMMWithParagraph(CoreMap paragraph){
        List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);

        int[] tokenType = new int[tokens.size()];
        for (int i = 0; i < tokenType.length; i++) {
            tokenType[i] =  DocumentAnnotatingHelper.getWordFeature(
                    paragraph, tokens.get(i), wordType);
        }

        int length = Math.min(hmm.size(), tokens.size());
        int[][] features = new int[length][wordFeatures.size()];
        for (int i=0; i<length ;i++){
            for (int f=0; f<wordFeatures.size();f++){
                features[i][f] = DocumentAnnotatingHelper.getWordFeature(paragraph, tokens.get(i), wordFeatures.get(f));
            }
        }

        hmm.updateCounts(
                DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
                tokenType, features);

    }
    void updateHMMWithParagraphAndWeight(CoreMap paragraph, double[] weights){
        List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);

        int[] tokenType = new int[tokens.size()];
        for (int i = 0; i < tokenType.length; i++) {

            tokenType[i] =  DocumentAnnotatingHelper.getWordFeature(
                    paragraph, tokens.get(i), wordType);
        }

        int length = Math.min(hmm.size(), tokens.size());
        int[][] features = new int[length][wordFeatures.size()];
        for (int i=0; i<length ;i++){
            for (int f=0; f<wordFeatures.size();f++){
                features[i][f] = DocumentAnnotatingHelper.getWordFeature(paragraph, tokens.get(i), wordFeatures.get(f));
            }
        }

        for (int i=0;i<paraCategory.getFeatureSize();i++) {
            hmm.updateCountsWithWeight(
                    DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
                    tokenType, features, weights[i]);
        }

    }
    /**
     * todo: to reduce memory usage at the cost of more computation, can process training paragraph one by one later instead of process all and store them now
    * training involves updating Fij for each paragraph i and feature j.
    */
    public void updateWithDocumentAndWeight(Document doc){
        List<CoreMap> paragraphs = new ArrayList<>();
        List<CoreMap> originalParagraphs = new ArrayList<>();

        for( CoreMap paragraph : doc.getParagraphs()) {
            if (DocumentAnnotatingHelper.isParaObserved(paragraph)) {
                paragraphs.add(DocumentAnnotatingHelper.processParagraph(paragraph, hmm.size()));
                originalParagraphs.add(paragraph);
            }
        }

        int[] docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(paragraphs,
                paraCategory, docFeatures, paraDocFeatures);

        //for( CoreMap paragraph : paragraphs)
        for (int i=0; i<paragraphs.size();i++) {

            updateWithParagraphWithWeights(paragraphs.get(i),originalParagraphs.get(i), docFeatureValues);
        }
    }

    public void updateWithDocument(Document doc){
        List<CoreMap> paragraphs = new ArrayList<>();
        for( CoreMap paragraph : doc.getParagraphs()) {
             paragraphs.add(DocumentAnnotatingHelper.processParagraph(paragraph, hmm.size()));
        }
        int[] docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(paragraphs,
                paraCategory, docFeatures, paraDocFeatures);


        for( CoreMap paragraph : paragraphs)
            updateWithParagraph(paragraph, docFeatureValues);
    }

    public TrainingNaiveBayesWithFeatureConditions getTnbfModel() {
        return tnbfModel;
    }

    @Override
    public String toString() {
        return "TrainingDocumentAnnotatingModel{" +
                "tnbfModel=" + tnbfModel +
                '}';
    }

    public HashMap<String, HashMap<String, HashMap<String, Double>>> toVisualMap() {
        HashMap<String, HashMap<String, HashMap<String, Double>>> map = new HashMap();
        //document level features
        map.put("DocuemntFeatureNodes", Visualizer.nodesToMap(this.tnbfModel.getDocumentFeatureNodeArray()));
        map.put("ParagraphFeatureNodes", Visualizer.nodesToMap(this.tnbfModel.getFeatureNodeArray()));
        return map;
    }

}


