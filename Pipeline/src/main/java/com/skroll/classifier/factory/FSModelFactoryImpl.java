package com.skroll.classifier.factory;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.classifier.ClassifierId;
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

    public TrainingDocumentAnnotatingModel getTrainingModel(ClassifierId classifierId, ModelRVSetting modelRVSetting) {
        if (TrainingModelMap.containsKey(classifierId.getId())){
            return TrainingModelMap.get(classifierId.getId());
        }
        TrainingDocumentAnnotatingModel model = createModel(classifierId, modelRVSetting);
        logger.debug("training model Map Size:{}",TrainingModelMap.size());
        logger.debug("bni model Map Size:{}",bniModelMap.size());
        return model;
    }

    public TrainingDocumentAnnotatingModel createModel(ClassifierId classifierId, ModelRVSetting modelRVSetting) {
        TrainingDocumentAnnotatingModel localTrainingModel = null;

        if (localTrainingModel == null) {
            try {
                    localTrainingModel = (TrainingDocumentAnnotatingModel) objectPersistUtil.readObject(null,classifierId.getName());

            } catch (Throwable e) {
                logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one" );
                localTrainingModel = null;
            }
        }
        if (localTrainingModel == null) {

            localTrainingModel = new TrainingDocumentAnnotatingModel(classifierId.getId(), modelRVSetting);
        }

        TrainingModelMap.put(classifierId.getId(), localTrainingModel);
        return localTrainingModel;
    }

    public String getBniId(ClassifierId classifierId, String documentId){
        return classifierId.getId() + "." + documentId;
    }

    @Override
    public ProbabilityDocumentAnnotatingModel createBNIModel(ClassifierId classifierId, String documentId, ModelRVSetting modelRVSetting, Document document) {

        TrainingDocumentAnnotatingModel tmpModel = createModel(classifierId, modelRVSetting);
        tmpModel.updateWithDocumentAndWeight(document);

        ProbabilityDocumentAnnotatingModel bniModel = new ProbabilityDocumentAnnotatingModel(classifierId.getId(), tmpModel.getNbmnModel(),
                tmpModel.getHmm(), document,modelRVSetting );
        bniModel.annotateDocument();
        //printBelieves(bniModel, document);

        bniModelMap.put(getBniId(classifierId,documentId),bniModel);

        return bniModel;
    }

    @Override
    public ProbabilityDocumentAnnotatingModel getBNIModel(ClassifierId classifierId, String documentId) {
        if (bniModelMap.containsKey(getBniId(classifierId,documentId))){
            return bniModelMap.get(getBniId(classifierId,documentId));
        }
        return null;
    }

    public void saveTrainingModel(ClassifierId classifierId, ModelRVSetting modelRVSetting) throws Exception {
        try {

            objectPersistUtil.persistObject(null, getTrainingModel(classifierId, modelRVSetting), classifierId.getName());
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            logger.error("failed to persist the model", e);
            throw new Exception(e);

        }
    }

}
