package com.skroll.classifier.factory;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.document.Document;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by saurabhagarwal on 4/25/15.
 */
public abstract class FSModelFactoryImpl implements ModelFactory {
    public static final Logger logger = LoggerFactory.getLogger(FSModelFactoryImpl.class);

    protected String modelFolderName = null;
    protected ObjectPersistUtil objectPersistUtil = null;
    protected  static Map<Integer, TrainingDocumentAnnotatingModel> TrainingModelMap = new HashMap<>();
    protected  static Map<Integer, ProbabilityDocumentAnnotatingModel> bniModelMap = new HashMap<>();


    public TrainingDocumentAnnotatingModel getTrainingModel(ModelRVSetting modelRVSetting) {
        if (TrainingModelMap.containsKey(modelRVSetting.getModelId())){
            return TrainingModelMap.get(modelRVSetting.getModelId());
        }
        TrainingDocumentAnnotatingModel model = createModel(modelRVSetting);
        logger.debug("training model Map Size:{}",TrainingModelMap.size());
        logger.debug("bni model Map Size:{}",bniModelMap.size());
        return model;
    }

    public TrainingDocumentAnnotatingModel createModel(ModelRVSetting modelRVSetting) {
        TrainingDocumentAnnotatingModel localTrainingModel = null;

        if (localTrainingModel == null) {
            try {
                    localTrainingModel = (TrainingDocumentAnnotatingModel) objectPersistUtil.readObject(null,String.valueOf(modelRVSetting.getModelId()));

            } catch (Throwable e) {
                logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one" );
                localTrainingModel = null;
            }
        }
        if (localTrainingModel == null) {

            localTrainingModel = new TrainingDocumentAnnotatingModel(modelRVSetting);
        }

        TrainingModelMap.put(modelRVSetting.getModelId(), localTrainingModel);
        return localTrainingModel;
    }

    public ProbabilityDocumentAnnotatingModel createBNIModel(ModelRVSetting modelRVSetting, Document document) {

        TrainingDocumentAnnotatingModel tmpModel = createModel(modelRVSetting);
        tmpModel.updateWithDocumentAndWeight(document);

        ProbabilityDocumentAnnotatingModel bniModel = new ProbabilityDocumentAnnotatingModel(tmpModel.getNbmnModel(),
                tmpModel.getHmm(), document,modelRVSetting );
        bniModel.annotateDocument();
        //printBelieves(bniModel, document);

        bniModelMap.put(modelRVSetting.getModelId(),bniModel);

        return bniModel;
    }

    public ProbabilityDocumentAnnotatingModel getBNIModel(ModelRVSetting modelRVSetting) {
        if (bniModelMap.containsKey(modelRVSetting.getModelId())){
            return bniModelMap.get(modelRVSetting.getModelId());
        }
        return null;
    }

    public void saveTrainingModel(ModelRVSetting modelRVSetting) throws Exception {
        try {

            objectPersistUtil.persistObject(null, getTrainingModel(modelRVSetting), String.valueOf(modelRVSetting.getModelId()));
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            logger.error("failed to persist the model", e);
            throw new Exception(e);

        }
    }

}
