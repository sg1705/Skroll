package com.skroll.classifier.factory;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
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
    public TrainingDocumentAnnotatingModel createModel(ModelRVSetting modelRVSetting);

    /**
     * Persist the training model into store
     * @param modelRVSetting
     * @throws Exception
     */
    public void saveTrainingModel(ModelRVSetting modelRVSetting) throws Exception;

    /**
     * Retrieve the training model from the store or cache
     * @param modelRVSetting
     * @return
     */
    public TrainingDocumentAnnotatingModel getTrainingModel(ModelRVSetting modelRVSetting);

    /**
     * Create the BNI model per document and update the probabilities of categories
     * @param modelRVSetting
     * @param document
     * @return
     */
    public ProbabilityDocumentAnnotatingModel createBNIModel(ModelRVSetting modelRVSetting, Document document);

    /**
     * retrieve the BNI model from cache
     * @param modelRVSetting
     * @return
     */
    public ProbabilityDocumentAnnotatingModel getBNIModel(ModelRVSetting modelRVSetting);
}
