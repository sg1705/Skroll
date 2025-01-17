package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.data.NBMNData;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.analyzer.model.bn.NBMNTuple;
import com.skroll.analyzer.model.bn.NBTrainingHelper;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class TrainingTextAnnotatingModel extends DocumentAnnotatingModel {

    public TrainingTextAnnotatingModel() {

    }

    public TrainingTextAnnotatingModel(int id, ModelRVSetting setting) {
        this(id, setting.getWordType(), setting.getWordFeatures(), setting.getNbmnConfig(), setting);
        this.id = id;
        modelRVSetting = setting;
    }

    private TrainingTextAnnotatingModel(int id, RandomVariable wordType,
                                        List<RandomVariable> wordFeatures,
                                        NBMNConfig nbmnConfig, ModelRVSetting modelRVSetting) {

        this(id, NBTrainingHelper.createTrainingNBMN(nbmnConfig),
                wordType, wordFeatures, nbmnConfig, modelRVSetting);

    }

    @JsonCreator
    public TrainingTextAnnotatingModel(
            @JsonProperty("id") int id,
            @JsonProperty("nbmnModel") NaiveBayesWithMultiNodes nbmnModel,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("nbmnConfig") NBMNConfig nbmnConfig,
            @JsonProperty("modelRVSetting") ModelRVSetting modelRVSetting) {
        this.id = id;
        this.nbmnConfig = nbmnConfig;
        this.nbmnModel = nbmnModel;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.modelRVSetting = modelRVSetting;
        int[] wordFeatureSizes = new int[wordFeatures.size()]; // include state at the feature index 0.
        for (int i = 0; i < wordFeatureSizes.length; i++)
            wordFeatureSizes[i] = wordFeatures.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                wordType.getFeatureSize(), wordFeatureSizes);
    }


    double[] getTrainingWeights(CoreMap para) {
        double[][] weights = modelRVSetting.modelClassAndWeightStrategy.populateTrainingWeights(para, modelRVSetting.getCategoryIds());

        double[] oldWeights = weights[0];
        double[] newWeights = weights[1];
        double[] normalizedOldWeights = BNInference.normalize(oldWeights, 1);
        double[] normalizedNewWeights = BNInference.normalize(newWeights, 1);
        double[] trainingWeights = normalizedNewWeights;
        for (int i = 0; i < trainingWeights.length; i++)
            trainingWeights[i] -= normalizedOldWeights[i];

        return trainingWeights;
    }

    //    //todo: need to use the right annotation for the weight.
    //todo: this method should be changed. Definitions should be annotated with good training data.
    void updateHMMWithParagraph(CoreMap originalPara, CoreMap processedPara) {
        List<Token> tokens = processedPara.get(CoreAnnotations.TokenAnnotation.class);
        int[] tokenType = new int[tokens.size()];


        for (int i = 0; i < tokenType.length; i++) {
            tokenType[i] = RVValues.getWordLevelRVValue(wordType, tokens.get(i), Arrays.asList(originalPara));
        }

        int length = Math.min(hmm.size(), tokens.size());
        int[][] features = new int[length][wordFeatures.size()];
        for (int i = 0; i < length; i++) {
            for (int f = 0; f < wordFeatures.size(); f++) {
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
    public void updateWithDocumentAndWeight(Document doc) {
        List<CoreMap> originalParas = doc.getParagraphs();

        // todo: the following two lines can cause a lot of inefficiency with the current approach of
        // updating training model with the whole doc each time user makes an observation.
//        List<CoreMap> processedParas = DocProcessor.processParas(doc, hmm.size());
        List<CoreMap> processedParas = DocProcessor.processParas(doc);
        modelRVSetting.postProcessFunctions
                .stream()
                .forEach(f -> f.apply(doc.getParagraphs(), processedParas));


        // in NBMNData, para features can be preprocessed for the whole doc,
        // but doc features depends on the set of the observed paras and cannot be preprocessed just once.
        NBMNData data = DocProcessor.getParaDataFromDoc(doc, nbmnConfig);

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
     * <p>
     * currently assuming the paras parameters contain all paragraphs in the doc.
     * todo: to improve performance, consider making viewer passing only observed paras.
     *
     * @param originalParas
     * @param processedParas
     * @param data
     */
    void updateWithProcessedParasAndWeight(List<CoreMap> originalParas,
                                                   List<CoreMap> processedParas, NBMNData data) {
//        List<CoreMap> originalParas = doc.getParagraphs();
        List<CoreMap> observedParas = modelRVSetting.modelClassAndWeightStrategy.getObservedParagraphs(originalParas);

        int[][] docFeatures = DocProcessor.generateDocumentFeatures(observedParas, data.getParaDocFeatures(), nbmnConfig);
        int[][] paraFeatures = data.getParaFeatures();
        int[][] paraDocFeatures = data.getParaDocFeatures();
        List<String[]>[] wordsList = data.getWordsLists();

        for (CoreMap op : observedParas) {
            int i = op.get(CoreAnnotations.IndexInteger.class);
            double[] weights = getTrainingWeights(op);
            int numCategories = nbmnConfig.getCategoryVar().getFeatureSize();
            for (int c = 0; c < numCategories; c++) {
//                int[] values = concatIntArrays(new int[]{c}, paraFeatures[i], paraDocFeatures[i], docFeatures);
//                NBTrainingHelper.addSample(nbmnModel, new NBMNTuple(wordsList[i], values), weights[c]);
                if (weights[c] == 0) continue;
                NBTrainingHelper.addSample(nbmnModel, new NBMNTuple(
                        wordsList[i], c, paraFeatures[i], paraDocFeatures[i], docFeatures), weights[c]);
            }

//            updateHMMWithParagraph(originalParas.get(i), processedParas.get(i));
        }

        for (int p = 0; p < originalParas.size(); p++) {
            if (modelRVSetting.modelClassAndWeightStrategy.isObserved(originalParas.get(p))) {
                updateHMMWithParagraph(originalParas.get(p), processedParas.get(p));
            }
        }

    }

    /**
     * the old method for training with doc. Does not use weight and go through all paragraphs, not just the observed.
     *
     * @param doc
     */
    public void updateWithDocument(Document doc) {


//        List<CoreMap> processedParas = DocProcessor.processParas(doc, hmm.size());
        List<CoreMap> processedParas = DocProcessor.processParas(doc);
        modelRVSetting.postProcessFunctions
                .stream()
                .forEach(f -> f.apply(doc.getParagraphs(), processedParas));

        NBMNData data = DocProcessor.getParaDataFromDoc(doc, nbmnConfig);
//        List<CoreMap> parasWithCategoryAnnotation = new ArrayList<>();
//        for (int categoryId : modelRVSetting.getCategoryIds()) // todo: inefficiency here. also better to keep paras in the same order.
//            parasWithCategoryAnnotation.addAll(CategoryAnnotationHelper.getParagraphsAnnotatedWithCategory(doc, categoryId));
//        updateWithDocument(parasWithCategoryAnnotation, processedParas, data);
        updateWithDocument(doc.getParagraphs(), processedParas, data);
    }

    public void updateWithDocument(List<CoreMap> originalParas, List<CoreMap> processedParas, NBMNData data) {
        int[][] docFeatures = DocProcessor.generateDocumentFeatures(originalParas, data.getParaDocFeatures(), nbmnConfig);
        for (int p = 0; p < originalParas.size(); p++) {
            CoreMap oPara = originalParas.get(p);

            int i = oPara.get(CoreAnnotations.IndexInteger.class);
            CoreMap pPara = processedParas.get(i);
            List<CoreMap> opParas = Arrays.asList(oPara, pPara);
            int categoryValue = RVValues.getValue(getParaCategory(), Arrays.asList(oPara));
            int[] paraFeatures = ParaProcessor.getFeatureVals(nbmnConfig.getFeatureVarList(), opParas);
            int[] paraDocFeatures =
                    ParaProcessor.getFeatureVals(nbmnConfig.getFeatureExistsAtDocLevelVarList(), opParas);
            List<String[]> wordsList = ParaProcessor.getWordsList(nbmnConfig.getWordVarList(), pPara);

//            int[] values = concatIntArrays(new int[]{categoryValue}, paraFeatures, paraDocFeatures, docFeatures);

            NBTrainingHelper.addSample(nbmnModel, new NBMNTuple(
                    wordsList, categoryValue, paraFeatures, paraDocFeatures, docFeatures));
//            NBTrainingHelper.addSample(nbmnModel, new NBMNTuple(wordsList, values));

            updateHMMWithParagraph(oPara, pPara);
        }

    }


    @Override
    public String toString() {
        return "TrainingDocumentAnnotatingModel{" +
                "nbmnModel=" + nbmnModel +
                "hmmModel=" + hmm +
                '}';
    }

    public HashMap<String, HashMap<String, HashMap<String, Double>>> toVisualMap() {
        HashMap<String, HashMap<String, HashMap<String, Double>>> map = new LinkedHashMap();
        return super.toVisualMap(map);
    }

    public boolean equals(TrainingTextAnnotatingModel model) {
        boolean isEqual = true;
        isEqual = isEqual && this.wordType.equals(model.wordType);
        isEqual = isEqual && RandomVariable.compareRVList(this.wordFeatures, model.wordFeatures);
        isEqual = isEqual && this.nbmnConfig.equals(model.nbmnConfig);
        isEqual = isEqual && this.nbmnModel.equals(model.getNbmnModel());
        return isEqual;
    }

}


