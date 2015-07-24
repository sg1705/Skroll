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

    //TOC Classifier
    public static final int TOC_CLASSIFIER_ID = 1;
    public static final String TOC_CLASSIFIER_NAME = "TOC_CLASSIFIER";
    public static final List<Integer> TOC_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.TOC_1,Category.TOC_2));
    public static final ClassifierProto tocClassifierProto = new ClassifierProto(TOC_CLASSIFIER_ID, TOC_CLASSIFIER_NAME,TOC_CATEGORY_IDS);

    //Def Classifier
    public static final int DEF_CLASSIFIER_ID = 2;
    public static final String DEF_CLASSIFIER_NAME = "DEF_CLASSIFIER";
    public static final List<Integer> DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.DEFINITION));
    public static final ClassifierProto defClassifierProto = new ClassifierProto(DEF_CLASSIFIER_ID, DEF_CLASSIFIER_NAME,DEF_CATEGORY_IDS);



    public Classifier getClassifier(int classifierId) throws Exception {
        ModelRVSetting modelRVSetting = null;
        /*
        if(modelRVSettings.containsKey(categoryId
            modelRVSetting = modelRVSettings.get(categoryId);
        } else {
            throw new Exception("No category id found: "+ categoryId);
        }
        */
        //TrainingDocumentAnnotatingModel trainingDocumentAnnotatingModel = modelFactory.getTrainingModel(modelRVSetting);
        Classifier classifier = null;
        if ( classifierId == 1) {
             classifier = new ClassifierImpl(tocClassifierProto, modelFactory);
        } else if (classifierId == 2){
             classifier = new ClassifierImpl(defClassifierProto, modelFactory);
        } else {
            throw new Exception ("Classifier Id: "+ classifierId + " is not supported");
        }
        classifiers.put(classifierId, classifier);
        return classifier;
    }

   //Todo: need to add the classifier per document. currently returning all classifiers no matter what document is.
   public List<Classifier> getClassifiers() throws Exception {
       return getClassifiers(null);
   }

    public List<Classifier> getClassifiers(Document document) throws Exception {
       List<Classifier> classifierList = new ArrayList<>();
       classifierList.add(getClassifier(TOC_CLASSIFIER_ID));
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
        getClassifier(TOC_CLASSIFIER_ID);
        //modelRVSettings.put(modelRVSetting.getId(), modelRVSetting);
    }
}
