package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.data.DocData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.analyzer.model.bn.NBTrainingHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.util.Visualizer;

import java.util.*;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class TrainingDocumentAnnotatingModel extends DocumentAnnotatingModel{

    NaiveBayesWithFeatureConditions tnbfModel;


    public TrainingDocumentAnnotatingModel() {
        this(new DefModelRVSetting());
    }

    public TrainingDocumentAnnotatingModel(ModelRVSetting setting) {
        this(setting.getWordType(), setting.getWordFeatures(), setting.getNbfcConfig());
    }

    public TrainingDocumentAnnotatingModel(RandomVariable wordType,
                                           List<RandomVariable> wordFeatures,
                                           NBFCConfig nbfcConfig ){

        this(NBTrainingHelper.createTrainingNBWithFeatureConditioning(nbfcConfig ),
                wordType, wordFeatures, nbfcConfig);

    }

    @JsonCreator
    public TrainingDocumentAnnotatingModel(
            @JsonProperty("tnbfModel")NaiveBayesWithFeatureConditions tnbfModel,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            NBFCConfig nbfcConfig){
        this.nbfcConfig = nbfcConfig;

        this.tnbfModel = tnbfModel;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;

        int []wordFeatureSizes = new int[wordFeatures.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  wordFeatures.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                wordType.getFeatureSize(), wordFeatureSizes);
        //initialize();


    }


    double[] getTrainingWeights(CoreMap para){
        double[][] weights = TrainingWeightAnnotationHelper.getParagraphWeight(para, nbfcConfig.getCategoryVar());
        double[] oldWeights =weights[0];
        double[] newWeights = weights[1];
        double[] normalizedOldWeights = BNInference.normalize(oldWeights, 1);
        double[] normalizedNewWeights = BNInference.normalize(newWeights, 1);
        double[] trainingWeights = normalizedNewWeights;
        for (int i=0; i<trainingWeights.length; i++)
            trainingWeights[i] -= normalizedOldWeights[i];

        return trainingWeights;
    }

    //    //todo: need to use the right annotation for the weight.
//    void updateTNBFWithParagraphAndWeight(CoreMap originalPara, CoreMap processedPara, int[] docFeatureValues, double[] weights) {
//        SimpleDataTuple dataTuple = DocumentAnnotatingHelper.makeDataTuple(originalPara, processedPara,
//                docFeatureValues, nbfcConfig);
//        int[] values = dataTuple.getDiscreteValues();
//        int numCategories = nbfcConfig.getCategoryVar().getFeatureSize();
//
//
//        for (int i = 0; i < numCategories; i++) {
//            values[0] = i;
//            NBTrainingHelper.addSample( tnbfModel, dataTuple, weights[i]);
//            //tnbfModel.addSample(dataTuple, weights[i]);
//        }
//    }
    //todo: this method should be changed. Definitions should be annotated with good training data.
    void updateHMMWithParagraph(CoreMap originalPara, CoreMap processedPara) {
        List<Token> tokens = processedPara.get(CoreAnnotations.TokenAnnotation.class);
        int[] tokenType = new int[tokens.size()];


        for (int i = 0; i < tokenType.length; i++) {
            tokenType[i] = RVValues.getWordLevelRVValue(wordType, tokens.get(i), originalPara);
        }

        int length = Math.min(hmm.size(), tokens.size());
        int[][] features = new int[length][wordFeatures.size()];
        for (int i=0; i<length ;i++){
            for (int f=0; f<wordFeatures.size();f++){
                features[i][f] = ParaProcessor.getWordFeatureValue(wordFeatures.get(f),
                        tokens.get(i), Arrays.asList(originalPara, processedPara));
            }
        }

        hmm.updateCounts(
                DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
                tokenType, features);

    }
//    void updateHMMWithParagraphAndWeight(CoreMap paragraph, double[] weights){
//        List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);
//
//        int[] tokenType = new int[tokens.size()];
//        for (int i = 0; i < tokenType.length; i++) {
//
//            tokenType[i] =  DocumentAnnotatingHelper.getWordFeature(
//                    paragraph, tokens.get(i), wordType);
//        }
//
//        int length = Math.min(hmm.size(), tokens.size());
//        int[][] features = new int[length][wordFeatures.size()];
//        for (int i=0; i<length ;i++){
//            for (int f=0; f<wordFeatures.size();f++){
//                features[i][f] = DocumentAnnotatingHelper.getWordFeature(paragraph, tokens.get(i), wordFeatures.get(f));
//            }
//        }
//
//        for (int i=0;i<nbfcConfig.getCategoryVar().getFeatureSize(); i++) {
//            hmm.updateCountsWithWeight(
//                    DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
//                    tokenType, features, weights[i]);
//        }
//
//    }

    /**
     * todo: to reduce memory usage at the cost of more computation, can process training paragraph one by one later instead of process all and store them now
    * training involves updating Fij for each paragraph i and feature j.
    */
    public void updateWithDocumentAndWeight(Document doc){
//        List<CoreMap> processedParas = new ArrayList<>();
//        List<CoreMap> originalParagraphs = new ArrayList<>();

        List<CoreMap> originalParas = doc.getParagraphs();
        List<CoreMap> processedParas = DocProcessor.processParagraphs(originalParas, hmm.size());
        DocData data = DocProcessor.getDataFromDoc(doc, processedParas, nbfcConfig);
        SimpleDataTuple[] tuples = data.getTuples();


        for (int p = 0; p < originalParas.size(); p++) {
            double[] weights = getTrainingWeights(originalParas.get(p));
            int[] values = tuples[p].getDiscreteValues();
            int numCategories = nbfcConfig.getCategoryVar().getFeatureSize();


            for (int i = 0; i < numCategories; i++) {
                values[0] = i;
                NBTrainingHelper.addSample(tnbfModel, tuples[p], weights[i]);
                //tnbfModel.addSample(dataTuple, weights[i]);
            }
            NBTrainingHelper.addSample(tnbfModel, tuples[p]);

        }

        for (int p = 0; p < originalParas.size(); p++) {
            updateHMMWithParagraph(originalParas.get(p), processedParas.get(p));
        }
    }

    public void updateWithDocument(Document doc){

        List<CoreMap> originalParas = doc.getParagraphs();
        List<CoreMap> processedParas = DocProcessor.processParagraphs(originalParas, hmm.size());
        DocData data = DocProcessor.getDataFromDoc(doc, processedParas, nbfcConfig);
        for (SimpleDataTuple tuple : data.getTuples())
            NBTrainingHelper.addSample(tnbfModel, tuple);
//        for (CoreMap para : processedParas)
//            updateHMMWithParagraph(para);
        for (int p = 0; p < originalParas.size(); p++) {
            updateHMMWithParagraph(originalParas.get(p), processedParas.get(p));
        }
//
//
//        for( CoreMap paragraph : originalParas) {
//             processedParas.add(DocumentAnnotatingHelper.processParagraph(paragraph, hmm.size()));
//        }
//        int[] docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(doc.getParagraphs(), processedParas,
//                nbfcConfig);
//
//
//        for (int i=0; i<processedParas.size(); i++)
//            updateWithParagraph(originalParas.get(i), processedParas.get(i), docFeatureValues);
////        for( CoreMap processedPara : processedParas)
////            updateWithParagraph(processedPara, docFeatureValues);
    }

    public NaiveBayesWithFeatureConditions getTnbfModel() {
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
        List<DiscreteNode> discreteNodes = this.tnbfModel.getAllDiscreteNodes();
        map.put("ParagraphFeatureNodes", Visualizer.nodesToMap(
                discreteNodes.toArray(new DiscreteNode[discreteNodes.size()])));
//        List<DiscreteNode> featureNodes = this.tnbfModel.getFeatureNodes();
//        map.put("ParagraphFeatureNodes", Visualizer.nodesToMap(
//                featureNodes.toArray(new DiscreteNode[featureNodes.size()])));
        return map;
    }

}


