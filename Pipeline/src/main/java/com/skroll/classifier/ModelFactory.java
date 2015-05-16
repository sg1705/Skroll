package com.skroll.classifier;

import com.skroll.analyzer.model.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.TrainingDocumentAnnotatingModel;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saurabhagarwal on 4/25/15.
 */
public class ModelFactory {
    public static final Logger logger = LoggerFactory.getLogger(ModelFactory.class);

    Configuration configuration = new Configuration();
    private String modelFolderName = configuration.get("modelFolder","/tmp");
    protected ObjectPersistUtil objectPersistUtil = new ObjectPersistUtil(modelFolderName);
    protected static Map<String, TrainingDocumentAnnotatingModel> TrainingModelMap = new HashMap<>();
    protected Map<String, ProbabilityDocumentAnnotatingModel> bniModelMap = new HashMap<>();

    TrainingDocumentAnnotatingModel getTrainingModel(Category category) {
        if (TrainingModelMap.containsKey(category.getName())){
            return TrainingModelMap.get(category.getName());
        }
        return createModel(category);
    }


    public TrainingDocumentAnnotatingModel createModel(Category category) {
        TrainingDocumentAnnotatingModel localTrainingModel =
                null;

        if (localTrainingModel == null) {
            try {

                    localTrainingModel = (TrainingDocumentAnnotatingModel) objectPersistUtil.readObject(null,category.getName());
            } catch (Throwable e) {
                logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one" );
                localTrainingModel = null;
            }
        }
        if (localTrainingModel == null) {

            localTrainingModel = new TrainingDocumentAnnotatingModel(category.wordType,
                    category.wordFeatures,
                    category.paraType,
                    category.paraFeatures,
                    category.paraDocFeatures,
                    category.docFeatures,
                    category.wordVarList);
        }

        TrainingModelMap.put(category.getName(), localTrainingModel);
        return localTrainingModel;
    }

    ProbabilityDocumentAnnotatingModel getBNIModel(Category category, Document document) {

        TrainingDocumentAnnotatingModel trainingModel = getTrainingModel(category);
        trainingModel.updateWithDocumentAndWeight(document);

        ProbabilityDocumentAnnotatingModel bniModel = new ProbabilityDocumentAnnotatingModel(trainingModel.getTnbfModel(),
                trainingModel.getHmm(), document, category.wordType, category.wordFeatures, category.paraType,category. paraFeatures,
                category.paraDocFeatures, category.docFeatures, category.wordVarList);
        bniModel.annotateDocument();
        //printBelieves(bniModel, document);
        return bniModel;
    }
    public void saveTrainingModel(Category category) throws ObjectPersistUtil.ObjectPersistException {
        objectPersistUtil.persistObject(null, getTrainingModel(category), category.getName());
    }

    void printBelieves(ProbabilityDocumentAnnotatingModel model, Document doc ){
        logger.trace("document level feature believes\n");

        double[][] dBelieves = model.getDocumentFeatureBelief();
        for (int i=0; i<dBelieves.length; i++){
            logger.trace(" " + model.DEFAULT_DOCUMENT_FEATURES);
            logger.trace(Arrays.toString(dBelieves[i]));
        }

        List<CoreMap> paraList = doc.getParagraphs();

        logger.trace("paragraph category believes\n");
        double[][] pBelieves = model.getParagraphCategoryBelief();

        for (int i=0; i<paraList.size(); i++){
            BNInference.normalizeLog(pBelieves[i]);
            logger.trace(paraList.get(i).getText());
            logger.trace(String.format("%d [%.0f %.0f]", i, pBelieves[i][0], pBelieves[i][1]));

        }
    }
}
