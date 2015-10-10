package com.skroll.classifier.factory;

import com.skroll.analyzer.model.applicationModel.*;
import com.skroll.analyzer.model.applicationModel.ProbabilityTextAnnotatingModel;
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
    protected static Map<Integer, TrainingTextAnnotatingModel> TrainingModelMap = new HashMap<>();
    protected static Map<String, ProbabilityTextAnnotatingModel> bniModelMap = new HashMap<>();

    public TrainingTextAnnotatingModel getTrainingModel(int modelId, ModelRVSetting modelRVSetting) {
        if (TrainingModelMap.containsKey(modelId)){
            return TrainingModelMap.get(modelId);
        }
        TrainingTextAnnotatingModel model = createModel(modelId, modelRVSetting);
        logger.debug("training model Map Size:{}",TrainingModelMap.size());
        logger.debug("bni model Map Size:{}",bniModelMap.size());
        return model;
    }

    public TrainingTextAnnotatingModel createModel(int classifierId, ModelRVSetting modelRVSetting) {
        TrainingTextAnnotatingModel localTrainingModel = null;

        if (localTrainingModel == null) {
            try {
                if (modelRVSetting instanceof TOCModelRVSetting)
                    localTrainingModel = (TrainingDocumentTOCAnnotatingModel) objectPersistUtil.readObject(
                            TrainingDocumentTOCAnnotatingModel.class, String.valueOf(classifierId));
                else
                    localTrainingModel = (TrainingTextAnnotatingModel) objectPersistUtil.readObject(
                            TrainingTextAnnotatingModel.class, String.valueOf(classifierId));

            } catch (Throwable e) {
                logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one" );
                localTrainingModel = null;
            }
        }
        if (localTrainingModel == null) {
            if (modelRVSetting instanceof TOCModelRVSetting)
                localTrainingModel = new TrainingDocumentTOCAnnotatingModel(classifierId, (TOCModelRVSetting) modelRVSetting);
            else
                localTrainingModel = new TrainingTextAnnotatingModel(classifierId, modelRVSetting);
        }

        TrainingModelMap.put(classifierId, localTrainingModel);
        return localTrainingModel;
    }

    public String getBniId(int modelId, String documentId) {
        return modelId + "." + documentId;
    }

    @Override
    public ProbabilityTextAnnotatingModel createBNIModel(int modelId, String documentId, ModelRVSetting modelRVSetting, Document document) {

        ProbabilityTextAnnotatingModel bniModel;
        if (modelRVSetting instanceof TOCModelRVSetting) {
            TrainingDocumentTOCAnnotatingModel tmpModel =
                    (TrainingDocumentTOCAnnotatingModel) createModel(modelId, modelRVSetting);
            tmpModel.updateWithDocumentAndWeight(document);
            bniModel = new ProbabilityDocumentTOCAnnotatingModel(
                    modelId, tmpModel.getNbmnModel(), tmpModel.getHmm(),
                    tmpModel.getSecNbmnModel(), tmpModel.getSecHmm(),
                    document, (TOCModelRVSetting) modelRVSetting);
        } else {
            TrainingTextAnnotatingModel tmpModel =
                    createModel(modelId, modelRVSetting);
            tmpModel.updateWithDocumentAndWeight(document);
            bniModel = new ProbabilityTextAnnotatingModel(
                    tmpModel.getNbmnModel(), tmpModel.getHmm(),
                    document, modelRVSetting);
        }


        bniModel.annotateParagraphs();
        //printBelieves(bniModel, document);

        bniModelMap.put(getBniId(modelId, documentId), bniModel);

        return bniModel;
    }

    @Override
    public ProbabilityTextAnnotatingModel getBNIModel(int modelId, String documentId) {
        if (bniModelMap.containsKey(getBniId(modelId, documentId))) {
            return bniModelMap.get(getBniId(modelId, documentId));
        }
        return null;
    }

    public void saveTrainingModel(int modelId, ModelRVSetting modelRVSetting) throws Exception {
        try {
            if (modelRVSetting instanceof TOCModelRVSetting)
                objectPersistUtil.persistObject(null, getTrainingModel(modelId, modelRVSetting),
                        String.valueOf(modelId));
            else
                objectPersistUtil.persistObject(null,
                        getTrainingModel(modelId, modelRVSetting), String.valueOf(modelId));

        } catch (ObjectPersistUtil.ObjectPersistException e) {
            logger.error("failed to persist the model", e);
            throw new Exception(e);

        }
    }

}
