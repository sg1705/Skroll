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
                DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL, DEFAULT_DOCUMENT_FEATURES, DEFAULT_WORDS);
    }

//    public TrainingDocumentAnnotatingModel(TrainingDocumentAnnotatingModel model){
//        this.tnbfModel = new TrainingNaiveBayesWithFeatureConditions(model.getTnbfModel());
//        this.hmm = new HiddenMarkovModel(model.getHMM());
//        this.wordType = model.wordType;
//        this.wordFeatures = model.wordFeatures;
//        this.paraCategory = model.paraCategory;
//        this.paraFeatures = model.paraFeatures;
//        this.paraDocFeatures = model.paraDocFeatures;
//        this.docFeatures = model.docFeatures;
//    }

    public TrainingDocumentAnnotatingModel(RandomVariableType wordType,
                                           List<RandomVariableType> wordFeatures,
                                           RandomVariableType paraCategory,
                                           List<RandomVariableType> paraFeatures,
                                           List<RandomVariableType> paraDocFeatures,
                                           List<RandomVariableType> docFeatures,
                                           List<RandomVariableType> wordVarList ){

        this(new TrainingNaiveBayesWithFeatureConditions(paraCategory,
                        paraFeatures, paraDocFeatures, docFeatures, wordVarList ),
                wordType, wordFeatures, paraCategory, paraFeatures, paraDocFeatures, docFeatures, wordVarList);

    }

    @JsonCreator
    public TrainingDocumentAnnotatingModel(
            @JsonProperty("tnbfModel")TrainingNaiveBayesWithFeatureConditions tnbfModel,
            @JsonProperty("wordType")RandomVariableType wordType,
            @JsonProperty("wordFeatures")List<RandomVariableType> wordFeatures,
            @JsonProperty("paraCategory")RandomVariableType paraCategory,
            @JsonProperty("paraFeatures")List<RandomVariableType> paraFeatures,
            @JsonProperty("paraDocFeatures")List<RandomVariableType> paraDocFeatures,
            @JsonProperty("docFeatures")List<RandomVariableType> docFeatures,
            @JsonProperty("wordVarList")List<RandomVariableType> wordVarList){

        this.tnbfModel = tnbfModel;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.paraCategory = paraCategory;
        this.paraFeatures = paraFeatures;
        this.paraDocFeatures = paraDocFeatures;
        this.docFeatures = docFeatures;
        this.wordVarList = wordVarList;

        int []wordFeatureSizes = new int[wordFeatures.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  wordFeatures.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                wordType.getFeatureSize(), wordFeatureSizes);
        initialize();


    }

    void updateWithParagraph(CoreMap originalPara, CoreMap processedPara, int[] docFeatureValues) {
        updateTNBFWithParagraph(originalPara, processedPara, docFeatureValues);

        //todo: check to see if HMM needs annotations from originalPara
       updateHMMWithParagraph(processedPara);
    }

    void updateWithParagraphWithWeights( CoreMap originalParagraph, CoreMap processedPara, int[] docFeatureValues) {
        double[] weights = getTrainingWeights(originalParagraph);
        updateTNBFWithParagraphAndWeight(originalParagraph, processedPara, docFeatureValues, weights);
        //todo: consider doing weight for HMM
        //todo: check if HMM needs info from originalPara
        updateHMMWithParagraph(processedPara);
        //updateHMMWithParagraphAndWeight(trainingParagraph,weights);
    }

    void updateTNBFWithParagraph(CoreMap originalPara, CoreMap processedPara, int[] docFeatureValues){
        SimpleDataTuple dataTuple = DocumentAnnotatingHelper.makeDataTuple(originalPara,processedPara, paraCategory,
                allParagraphFeatures, docFeatureValues, wordVarList);
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
    void updateTNBFWithParagraphAndWeight(CoreMap originalPara, CoreMap processedPara, int[] docFeatureValues, double[] weights) {
        SimpleDataTuple dataTuple = DocumentAnnotatingHelper.makeDataTuple(originalPara, processedPara, paraCategory,
                allParagraphFeatures, docFeatureValues, wordVarList);
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
        List<CoreMap> processedParas = new ArrayList<>();
        List<CoreMap> originalParagraphs = new ArrayList<>();

        for( CoreMap paragraph : doc.getParagraphs()) {
            if (DocumentAnnotatingHelper.isParaObserved(paragraph)) {
                processedParas.add(DocumentAnnotatingHelper.processParagraph(paragraph, hmm.size()));
                originalParagraphs.add(paragraph);
            }
        }

        int[] docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(originalParagraphs,processedParas,
                paraCategory, docFeatures, paraDocFeatures);

        //for( CoreMap paragraph : paragraphs)
        for (int i=0; i<processedParas.size();i++) {

            updateWithParagraphWithWeights(originalParagraphs.get(i), processedParas.get(i), docFeatureValues);
        }
    }

    public void updateWithDocument(Document doc){
        List<CoreMap> processedParas = new ArrayList<>();
        List<CoreMap> originalParas = doc.getParagraphs();
        for( CoreMap paragraph : originalParas) {
             processedParas.add(DocumentAnnotatingHelper.processParagraph(paragraph, hmm.size()));
        }
        int[] docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(doc.getParagraphs(), processedParas,
                paraCategory, docFeatures, paraDocFeatures);


        for (int i=0; i<processedParas.size(); i++)
            updateWithParagraph(originalParas.get(i), processedParas.get(i), docFeatureValues);
//        for( CoreMap processedPara : processedParas)
//            updateWithParagraph(processedPara, docFeatureValues);
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
//        map.put("DocuemntFeatureNodes", Visualizer.nodesToMap(this.tnbfModel.getDocumentFeatureNodeArray()));
//        map.put("ParagraphFeatureNodes", Visualizer.nodesToMap(this.tnbfModel.getFeatureNodeArray()));
        map.put("ParagraphFeatureNodes", Visualizer.nodesToMap(this.tnbfModel.getDiscreteNodeArray()));

        return map;
    }

}


