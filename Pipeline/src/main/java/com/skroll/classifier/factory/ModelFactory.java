package com.skroll.classifier.factory;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.document.Document;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by saurabhagarwal on 4/25/15.
 */
public interface ModelFactory {
    public static final Logger logger = LoggerFactory.getLogger(ModelFactory.class);

    public TrainingDocumentAnnotatingModel createModel(ModelRVSetting modelRVSetting);

    public void saveTrainingModel(ModelRVSetting modelRVSetting) throws ObjectPersistUtil.ObjectPersistException;

    public TrainingDocumentAnnotatingModel getTrainingModel(ModelRVSetting modelRVSetting);

    public ProbabilityDocumentAnnotatingModel createBNIModel(ModelRVSetting modelRVSetting, Document document);

    public ProbabilityDocumentAnnotatingModel getBNIModel(ModelRVSetting modelRVSetting);
}
