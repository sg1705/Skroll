package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.Document;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabh on 4/16/15.
 */
public class ClassifierFactory {

    private static HashMap<Integer, Classifier> classifiers = new HashMap();
    @Inject
    private ModelFactory modelFactory;
    public static final int CLASSIFIER_TOC = 1;
    public static final String CLASSIFIER_TOC_NAME = "TOC_CLASSIFIER";
    public static final List<Integer> TOC_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.TOC_1,Category.TOC_2));
    public Classifier getClassifier(int classifierId) {
        ModelRVSetting modelRVSetting = null;
        /*
        if(modelRVSettings.containsKey(categoryId)){
            modelRVSetting = modelRVSettings.get(categoryId);
        } else {
            throw new Exception("No category id found: "+ categoryId);
        }
        */
        //TrainingDocumentAnnotatingModel trainingDocumentAnnotatingModel = modelFactory.getTrainingModel(modelRVSetting);

        if (classifiers.containsKey(classifierId))
            return classifiers.get(classifierId);
        Classifier classifier = new ClassifierImpl(classifierId, CLASSIFIER_TOC_NAME, TOC_CATEGORY_IDS, modelFactory);

        classifiers.put(classifierId, classifier);
        return classifier;
    }

   //Todo: need to add the classifier per document. currently returning all classifiers no matter what document is.
   public List<Classifier> getClassifiers() throws Exception {
       return getClassifiers(null);
   }

    public List<Classifier> getClassifiers(Document document) throws Exception {
       List<Classifier> classifierList = new ArrayList<>();
       classifierList.add(getClassifier(CLASSIFIER_TOC));
       //classifierList.add(getClassifier(Category.TOC_2));
       //classifierList.add(getClassifier(Category.TOC_3));
       //classifierList.add(getClassifier(Category.TOC_4));
       //classifierList.add(getClassifier(Category.TOC_5));
       return classifierList;
    }

    /**
     * Creates classifier for a given modelRVSetting
     *
     */
    public void createClassifier() throws Exception {
        getClassifier(CLASSIFIER_TOC);
        //modelRVSettings.put(modelRVSetting.getClassifierId(), modelRVSetting);
    }
}
