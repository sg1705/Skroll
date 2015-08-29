package com.skroll.classifier.factory;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.classifier.ClassifierId;
import com.skroll.document.Document;

/**
 * Created by saurabhagarwal on 4/25/15.
 */
public interface ModelFactory {

    /**
     * Create new Training Model using ModelRVSetting
     *
     * @param classifierId
     * @param modelRVSetting
     * @return
     */
    public TrainingDocumentAnnotatingModel createModel(ClassifierId classifierId, ModelRVSetting modelRVSetting);

    /**
     * Persist the training model into store
     * @param modelRVSetting
     * @throws Exception
     */
    public void saveTrainingModel(ClassifierId classifierId, ModelRVSetting modelRVSetting) throws Exception;

    /**
     * Retrieve the training model from the store or cache
     * @param modelRVSetting
     * @return
     */
    public TrainingDocumentAnnotatingModel getTrainingModel(ClassifierId classifierId, ModelRVSetting modelRVSetting);

    /**
     * Create the BNI model per document and update the probabilities of categories
     * @param modelRVSetting
     * @param document
     * @return
     */
    public ProbabilityDocumentAnnotatingModel createBNIModel(ClassifierId classifierId, String documentId, ModelRVSetting modelRVSetting, Document document);

    /**
     * retrieve the BNI model from cache
     * @return
     */
    public ProbabilityDocumentAnnotatingModel getBNIModel(ClassifierId classifierId, String documentId);
}
