package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TOCModelRVSetting;
import com.skroll.classifier.factory.ModelFactory;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by saurabh on 4/16/15.
 */
public class ClassifierFactory {

    private static HashMap<Integer, Classifier> classifiers = new HashMap();
    @Inject
    private ModelFactory modelFactory;

    //TOC Classifier
    public static final int TOC_CLASSIFIER_ID = 1;
    private static final List<Integer> TOC_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE, Category.TOC_1, Category.TOC_2));
    private static final ClassifierProto tocClassifierProto = new ClassifierProto(TOC_CLASSIFIER_ID,TOC_CATEGORY_IDS);

    //Def Classifier
    public static final int DEF_CLASSIFIER_ID = 2;
    private static final List<Integer> DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.DEFINITION));
    private static final ClassifierProto defClassifierProto = new ClassifierProto(DEF_CLASSIFIER_ID,DEF_CATEGORY_IDS);

    public Classifier getClassifier(int classifierId) {
        Classifier classifier;
        if (classifiers.containsKey(classifierId))
            return classifiers.get(classifierId);

        if (classifierId == TOC_CLASSIFIER_ID) {
                classifier = new ClassifierImpl(TOC_CLASSIFIER_ID, tocClassifierProto, modelFactory, new TOCModelRVSetting(tocClassifierProto.getCategoryIds()));
        } else if (classifierId == DEF_CLASSIFIER_ID){
             classifier = new ClassifierImpl (DEF_CLASSIFIER_ID,defClassifierProto, modelFactory, new DefModelRVSetting(defClassifierProto.getCategoryIds()));
        } else {
            throw new RuntimeException("Classifier Id: "+ classifierId + " is not supported");
        }
        classifiers.put(classifierId, classifier);
        return classifier;
    }

    public List<Classifier> getClassifiers(ClassifierFactoryStrategy classifierFactoryStrategy) throws Exception {
        return classifierFactoryStrategy.getClassifierIds()
                .stream()
                .map(classifierId -> this.getClassifier(classifierId))
                .collect(Collectors.toList());
    }
}
