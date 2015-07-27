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


    public TrainingDocumentAnnotatingModel getTrainingModel(int modelId, ModelRVSetting modelRVSetting) {
        if (TrainingModelMap.containsKey(modelId)){
            return TrainingModelMap.get(modelId);
        }
        TrainingDocumentAnnotatingModel model = createModel(modelId, modelRVSetting);
        logger.debug("training model Map Size:{}",TrainingModelMap.size());
        logger.debug("bni model Map Size:{}",bniModelMap.size());
        return model;
    }

    public TrainingDocumentAnnotatingModel createModel(int modelId, ModelRVSetting modelRVSetting) {
        TrainingDocumentAnnotatingModel localTrainingModel = null;

        if (localTrainingModel == null) {
            try {
                    localTrainingModel = (TrainingDocumentAnnotatingModel) objectPersistUtil.readObject(null,String.valueOf(modelId));

            } catch (Throwable e) {
                logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one" );
                localTrainingModel = null;
            }
        }
        if (localTrainingModel == null) {

            localTrainingModel = new TrainingDocumentAnnotatingModel(modelId, modelRVSetting);
        }

        TrainingModelMap.put(modelId, localTrainingModel);
        return localTrainingModel;
    }

    public ProbabilityDocumentAnnotatingModel createBNIModel(int modelId, ModelRVSetting modelRVSetting, Document document) {

        TrainingDocumentAnnotatingModel tmpModel = createModel(modelId, modelRVSetting);
        tmpModel.updateWithDocumentAndWeight(document);

        ProbabilityDocumentAnnotatingModel bniModel = new ProbabilityDocumentAnnotatingModel(modelId, tmpModel.getNbmnModel(),
                tmpModel.getHmm(), document,modelRVSetting );
        bniModel.annotateDocument();
        //printBelieves(bniModel, document);

        bniModelMap.put(modelId,bniModel);

        return bniModel;
    }

    public ProbabilityDocumentAnnotatingModel getBNIModel(int modelId) {
        if (bniModelMap.containsKey(modelId)){
            return bniModelMap.get(modelId);
        }
        return null;
    }

    public void saveTrainingModel(int modelId, ModelRVSetting modelRVSetting) throws Exception {
        try {

            objectPersistUtil.persistObject(null, getTrainingModel(modelId, modelRVSetting), String.valueOf(modelId));
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            logger.error("failed to persist the model", e);
            throw new Exception(e);

        }
    }

}
