package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.data.DocData;
import com.skroll.analyzer.data.NBFCData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.analyzer.model.bn.NBTrainingHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.classifier.Category;
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

    @JsonProperty("tnbfModel")
    NaiveBayesWithFeatureConditions tnbfModel;


    public TrainingDocumentAnnotatingModel() {
        this(new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME));

    }

    public TrainingDocumentAnnotatingModel(ModelRVSetting setting) {
        this(setting.getWordType(), setting.getWordFeatures(), setting.getNbfcConfig(),setting);
        modelRVSetting = setting;
    }

    private TrainingDocumentAnnotatingModel(RandomVariable wordType,
                                           List<RandomVariable> wordFeatures,
                                           NBFCConfig nbfcConfig,ModelRVSetting modelRVSetting ){

        this(NBTrainingHelper.createTrainingNBWithFeatureConditioning(nbfcConfig ),
                wordType, wordFeatures, nbfcConfig,modelRVSetting);

    }

    @JsonCreator
    public TrainingDocumentAnnotatingModel(
            @JsonProperty("tnbfModel")NaiveBayesWithFeatureConditions tnbfModel,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("nbfcConfig")NBFCConfig nbfcConfig,
            @JsonProperty("ModelRVSetting")ModelRVSetting modelRVSetting){
        this.nbfcConfig = nbfcConfig;

        this.tnbfModel = tnbfModel;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.modelRVSetting = modelRVSetting;
        int []wordFeatureSizes = new int[wordFeatures.size()]; // include state at the feature index 0.
        for (int i=0; i<wordFeatureSizes.length;i++)
            wordFeatureSizes[i] =  wordFeatures.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                wordType.getFeatureSize(), wordFeatureSizes);
    }


    double[] getTrainingWeights(CoreMap para){
        double[][] weights = TrainingWeightAnnotationHelper.getParagraphWeight(para, nbfcConfig.getCategoryVar(), modelRVSetting.getCategoryId());
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


    /**
     * todo: to reduce memory usage at the cost of more computation, can process training paragraph one by one later instead of process all and store them now
    * training involves updating Fij for each paragraph i and feature j.
    */
    public void updateWithDocumentAndWeight(Document doc){
        List<CoreMap> originalParas = doc.getParagraphs();

        // todo: the following two lines can cause a lot of inefficiency with the current approach of
        // updating training model with the whole doc each time user makes an observation.
        List<CoreMap> processedParas = DocProcessor.processParagraphs(originalParas, hmm.size());

        // in NBFCData, para features can be preprocessed for the whole doc,
        // but doc features depends on the set of the observed paras and cannot be preprocessed just once.
        NBFCData data = DocProcessor.getParaDataFromDoc(doc.getParagraphs(), processedParas, nbfcConfig);

        updateWithProcessedParasAndWeight(originalParas, processedParas, data);

    }

    public int[] concatIntArrays(int[]... intArrays) {
        int totalLen = 0;
        for (int[] intArray : intArrays) totalLen += intArray.length;

        int[] result = new int[totalLen];
        int i = 0;
        for (int[] intArray : intArrays) {
            for (int n : intArray)
                result[i++] = n;
        }
        return result;
    }


    /**
     * This method can be used with already processed paras, and precomputed feature values for better efficiency.
     * <p/>
     * currently assuming the paras parameters contain all paragraphs in the doc.
     * todo: to improve performance, consider making viewer passing only observed paras.
     *
     * @param originalParas
     * @param processedParas
     * @param data
     */
    public void updateWithProcessedParasAndWeight(List<CoreMap> originalParas,
                                                  List<CoreMap> processedParas, NBFCData data) {
//        List<CoreMap> originalParas = doc.getParagraphs();
        if (processedParas == null) processedParas = DocProcessor.processParagraphs(originalParas, hmm.size());
        if (data == null) data = DocProcessor.getParaDataFromDoc(originalParas, processedParas, nbfcConfig);
        List<CoreMap> observedParas = DocumentHelper.getObservedParagraphs(originalParas);

        int[] docFeatures = DocProcessor.generateDocumentFeatures(observedParas, data.getParaFeatures(), nbfcConfig);
        int[][] paraFeatures = data.getParaFeatures();
        int[][] paraDocFeatures = data.getParaDocFeatures();
        List<String[]>[] wordsList = data.getWordsLists();

        for (CoreMap op : observedParas) {
            int i = op.get(CoreAnnotations.IndexInteger.class);
            double[] weights = getTrainingWeights(op);
            int numCategories = nbfcConfig.getCategoryVar().getFeatureSize();
            for (int c = 0; c < numCategories; c++) {
                int[] values = concatIntArrays(new int[]{c}, paraFeatures[i], paraDocFeatures[i], docFeatures);
                NBTrainingHelper.addSample(tnbfModel, new SimpleDataTuple(wordsList[i], values), weights[c]);
            }

        }

        for (int p = 0; p < originalParas.size(); p++) {
            updateHMMWithParagraph(originalParas.get(p), processedParas.get(p));
        }
    }

    /**
     * the old method for training with doc. Does not use weight and go through all paragraphs, not just the observed.
     * @param doc
     */
    public void updateWithDocument(Document doc){

        List<CoreMap> originalParas = doc.getParagraphs();
        List<CoreMap> processedParas = DocProcessor.processParagraphs(originalParas, hmm.size());
        NBFCData data = DocProcessor.getParaDataFromDoc(doc.getParagraphs(), processedParas, nbfcConfig);
        updateWithDocument(originalParas, processedParas, data);
    }

    public void updateWithDocument(List<CoreMap> originalParas, List<CoreMap> processedParas, NBFCData data) {
        int[] docFeatures = DocProcessor.generateDocumentFeatures(originalParas, data.getParaDocFeatures(), nbfcConfig);
        for (int p = 0; p < originalParas.size(); p++) {
            CoreMap oPara = originalParas.get(p);
            CoreMap pPara = processedParas.get(p);
            List<CoreMap> opParas = Arrays.asList(oPara, pPara);
            int categoryValue = RVValues.getValue(getParaCategory(), oPara);
            int[] paraFeatures = ParaProcessor.getFeatureVals(nbfcConfig.getFeatureVarList(), opParas);
            int[] paraDocFeatures =
                    ParaProcessor.getFeatureVals(nbfcConfig.getFeatureExistsAtDocLevelVarList(), opParas);
            List<String[]> wordsList = ParaProcessor.getWordsList(nbfcConfig.getWordVarList(), pPara);

            int[] values = concatIntArrays(new int[]{categoryValue}, paraFeatures, paraDocFeatures, docFeatures);

            NBTrainingHelper.addSample(tnbfModel, new SimpleDataTuple(wordsList, values));

            updateHMMWithParagraph(oPara, pPara);
        }

    }

    public NaiveBayesWithFeatureConditions getTnbfModel() {
        return tnbfModel;
    }

    @Override
    public String toString() {
        return "TrainingDocumentAnnotatingModel{" +
                "tnbfModel=" + tnbfModel +
                        "hmmModel=" + hmm +
                '}';
    }

    public HashMap<String, HashMap<String, HashMap<String, Double>>> toVisualMap() {
        HashMap<String, HashMap<String, HashMap<String, Double>>> map = new HashMap();
        //document level features
        List<DiscreteNode> discreteNodes = this.tnbfModel.getAllDiscreteNodes();
        map.put("ParagraphFeatureNodes", Visualizer.nodesToMap(
                discreteNodes.toArray(new DiscreteNode[discreteNodes.size()])));
        return map;
    }

    public boolean equals(TrainingDocumentAnnotatingModel model) {
        boolean isEqual = true;
        isEqual = isEqual && this.wordType.equals(model.wordType);
        isEqual = isEqual && RandomVariable.compareRVList(this.wordFeatures, model.wordFeatures);
        isEqual = isEqual && this.nbfcConfig.equals(model.nbfcConfig);
        isEqual = isEqual && this.tnbfModel.equals(model.getTnbfModel());
        return isEqual;
    }

}


