package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TOCModelRVSetting;
import com.skroll.classifier.factory.ModelFactory;
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
    @Inject
    private ModelFactory modelFactory;

    public Classifier getClassifier(int classifierId) throws Exception {
        Classifier classifier = null;
        if ( classifierId == Classifiers.TOC_CLASSIFIER_ID) {
             classifier = new ClassifierImpl(Classifiers.TOC_CLASSIFIER_ID, Classifiers.tocClassifierProto, modelFactory, new TOCModelRVSetting(Classifiers.tocClassifierProto.getCategoryIds()));
        } else if (classifierId == Classifiers.DEF_CLASSIFIER_ID){
             classifier = new ClassifierImpl(Classifiers.DEF_CLASSIFIER_ID, Classifiers.defClassifierProto, modelFactory, new DefModelRVSetting(Classifiers.defClassifierProto.getCategoryIds()));
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
       classifierList.add(getClassifier(Classifiers.TOC_CLASSIFIER_ID));
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
        getClassifier(Classifiers.TOC_CLASSIFIER_ID);
        //modelRVSettings.put(modelRVSetting.getId(), modelRVSetting);
    }
}
