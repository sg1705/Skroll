package com.skroll.classifier.factory;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ProbabilityTextAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingTextAnnotatingModel;
import com.skroll.document.Document;

/**
 * Created by saurabhagarwal on 4/25/15.
 */
public interface ModelFactory {

    /**
     * Create new Training Model using ModelRVSetting
     * @param modelRVSetting
     * @return
     */
    public TrainingTextAnnotatingModel createModel(int modelId, ModelRVSetting modelRVSetting);

    /**
     * Persist the training model into store
     * @param modelRVSetting
     * @throws Exception
     */
    public void saveTrainingModel(int modelId, ModelRVSetting modelRVSetting) throws Exception;

    /**
     * Retrieve the training model from the store or cache
     * @param modelRVSetting
     * @return
     */
    public TrainingTextAnnotatingModel getTrainingModel(int modelId, ModelRVSetting modelRVSetting);

    /**
     * Create the BNI model per document and update the probabilities of categories
     * @param modelRVSetting
     * @param document
     * @return
     */
    public ProbabilityTextAnnotatingModel createBNIModel(int modelId, String documentId, ModelRVSetting modelRVSetting, Document document);

    /**
     * retrieve the BNI model from cache
     * @return
     */
    public ProbabilityTextAnnotatingModel getBNIModel(int modelId, ModelRVSetting modelRVSetting, Document document);
}
