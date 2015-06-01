package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TOCModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.document.Document;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabh on 4/16/15.
 */
public class ClassifierFactory {

    private static HashMap<Integer, Classifier> classifiers = new HashMap();
    private static HashMap<Integer, ModelRVSetting> modelRVSettings = new HashMap();
    @Inject
    private ModelFactory modelFactory;

    static {
        modelRVSettings.put(Category.DEFINITION, new DefModelRVSetting(Category.DEFINITION, Category.DEFINITION_NAME));
        modelRVSettings.put(Category.TOC_1, new TOCModelRVSetting(Category.TOC_1, Category.TOC_1_NAME));
        modelRVSettings.put(Category.TOC_2, new TOCModelRVSetting(Category.TOC_2, Category.TOC_2_NAME));
        modelRVSettings.put(Category.TOC_3, new TOCModelRVSetting(Category.TOC_3, Category.TOC_3_NAME));
        modelRVSettings.put(Category.TOC_4, new TOCModelRVSetting(Category.TOC_4, Category.TOC_4_NAME));
        modelRVSettings.put(Category.TOC_5, new TOCModelRVSetting(Category.TOC_5, Category.TOC_5_NAME));
    }

    public Classifier getClassifier(int categoryId) throws Exception {
        ModelRVSetting modelRVSetting = null;
        if(modelRVSettings.containsKey(categoryId)){
            modelRVSetting = modelRVSettings.get(categoryId);
        } else {
            throw new Exception("No category id found: "+ categoryId);
        }
        TrainingDocumentAnnotatingModel trainingDocumentAnnotatingModel = modelFactory.getTrainingModel(modelRVSetting);
        if (classifiers.containsKey(categoryId))
            return classifiers.get(categoryId);
        Classifier classifier =
                new ClassifierImpl(modelFactory, trainingDocumentAnnotatingModel, modelRVSetting);
        classifiers.put(categoryId, classifier);
        return classifier;
    }
   //Todo: need to add the classifier per document. currently returning all classifiers no matter what document is.
   public List<Classifier> getClassifier(Document document) throws Exception {
       List<Classifier> classifierList = new ArrayList<>();
       classifierList.add(getClassifier(Category.DEFINITION));
       classifierList.add(getClassifier(Category.TOC_1));
       classifierList.add(getClassifier(Category.TOC_2));
       classifierList.add(getClassifier(Category.TOC_3));
       classifierList.add(getClassifier(Category.TOC_4));
       classifierList.add(getClassifier(Category.TOC_5));
       return classifierList;
   }
}
