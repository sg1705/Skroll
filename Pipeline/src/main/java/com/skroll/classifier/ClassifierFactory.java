package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TOCModelRVSetting;
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
    private  static final int TOC_CLASSIFIER_ID = 1;
    private static final List<Integer> TOC_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE, Category.TOC_1, Category.TOC_2));
    private static final ClassifierProto tocClassifierProto = new ClassifierProto(TOC_CLASSIFIER_ID,TOC_CATEGORY_IDS);

    //Def Classifier
    private static final int DEF_CLASSIFIER_ID = 2;
    private static final List<Integer> DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.DEFINITION));
    private static final ClassifierProto defClassifierProto = new ClassifierProto(DEF_CLASSIFIER_ID,DEF_CATEGORY_IDS);


    public Classifier getClassifier(int classifierId) throws Exception {
        Classifier classifier = null;
        if ( classifierId == TOC_CLASSIFIER_ID) {
             classifier = new ClassifierImpl(TOC_CLASSIFIER_ID, tocClassifierProto, modelFactory, new TOCModelRVSetting(tocClassifierProto.getCategoryIds()));
        } else if (classifierId == DEF_CLASSIFIER_ID){
             classifier = new ClassifierImpl(DEF_CLASSIFIER_ID,defClassifierProto, modelFactory, new DefModelRVSetting(defClassifierProto.getCategoryIds()));
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
        classifierList.add(getClassifier(DEF_CLASSIFIER_ID));

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
