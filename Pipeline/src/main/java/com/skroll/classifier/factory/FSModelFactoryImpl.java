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
    protected  static Map<String, ProbabilityDocumentAnnotatingModel> bniModelMap = new HashMap<>();

    public TrainingDocumentAnnotatingModel getTrainingModel(int modelId, ModelRVSetting modelRVSetting) {
        if (TrainingModelMap.containsKey(modelId)){
            return TrainingModelMap.get(modelId);
        }
        TrainingDocumentAnnotatingModel model = createModel(modelId, modelRVSetting);
        logger.debug("training model Map Size:{}",TrainingModelMap.size());
        logger.debug("bni model Map Size:{}",bniModelMap.size());
        return model;
    }

    public TrainingDocumentAnnotatingModel createModel(int classifierId, ModelRVSetting modelRVSetting) {
        TrainingDocumentAnnotatingModel localTrainingModel = null;

        if (localTrainingModel == null) {
            try {
                    localTrainingModel = (TrainingDocumentAnnotatingModel) objectPersistUtil.readObject(null,String.valueOf(classifierId));

            } catch (Throwable e) {
                logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one" );
                localTrainingModel = null;
            }
        }
        if (localTrainingModel == null) {

            localTrainingModel = new TrainingDocumentAnnotatingModel(classifierId, modelRVSetting);
        }

        TrainingModelMap.put(classifierId, localTrainingModel);
        return localTrainingModel;
    }

    public String getBniId(int modelId, String documentId){
        return modelId + "." + documentId;
    }

    @Override
    public ProbabilityDocumentAnnotatingModel createBNIModel(int modelId, String documentId, ModelRVSetting modelRVSetting, Document document) {

        TrainingDocumentAnnotatingModel tmpModel = createModel(modelId, modelRVSetting);
        tmpModel.updateWithDocumentAndWeight(document);

        ProbabilityDocumentAnnotatingModel bniModel = new ProbabilityDocumentAnnotatingModel(modelId, tmpModel.getNbmnModel(),
                tmpModel.getHmm(), document,modelRVSetting );
        bniModel.annotateDocument();
        //printBelieves(bniModel, document);

        bniModelMap.put(getBniId(modelId,documentId),bniModel);

        return bniModel;
    }

    @Override
    public ProbabilityDocumentAnnotatingModel getBNIModel(int modelId, String documentId) {
        if (bniModelMap.containsKey(getBniId(modelId,documentId))){
            return bniModelMap.get(getBniId(modelId,documentId));
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
