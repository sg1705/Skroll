package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.DefModelRVSetting;
import com.skroll.analyzer.model.applicationModel.DocTypeModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TOCModelRVSetting;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.Document;

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


    //DocType Classifier
    public static final int DOCTYPE_CLASSIFIER_ID = 1;
    private static final List<Integer> DOCTYPE_IDS = new ArrayList<>(Category.getDocType());

    //TOC Classifier
    public static final int UNIVERSAL_TOC_CLASSIFIER_ID = 100;
    public static final int TEN_K_TOC_CLASSIFIER_ID = 101;
    public static final int TEN_Q_TOC_CLASSIFIER_ID = 102;
    public static final int INDENTURE_TOC_CLASSIFIER_ID = 103;
    public static final int S4_TOC_CLASSIFIER_ID = 104;
    private static final List<Integer> TOC_CATEGORY_IDS = new ArrayList<>(Arrays.asList(Category.NONE, Category.TOC_1, Category.TOC_2, Category.USER_TOC));
    private static final double[] TOC_ANNOTATING_THRESHOLD= new double[]{0, .999999, 2, 0.99999}; //disable level 2 annotation in the doc model.
    public static final List<Integer> LOWER_TOC_CATEGORY_IDS = new ArrayList<>(Arrays.asList(Category.NONE, Category.TOC_2));
    private static final double[] LOWER_TOC_ANNOTATING_THRESHOLD= new double[]{0, .9}; //disable level 2 annotation in the doc model.


    //Def Classifier
    public static final int UNIVERSAL_DEF_CLASSIFIER_ID = 200;
    public static final int TEN_K_DEF_CLASSIFIER_ID = 201;
    public static final int TEN_Q_DEF_CLASSIFIER_ID = 202;
    public static final int INDENTURE_DEF_CLASSIFIER_ID = 203;
    public static final int S4_DEF_CLASSIFIER_ID = 204;
    private static final List<Integer> DEF_CATEGORY_IDS = new ArrayList<>(Arrays.asList(Category.NONE, Category.DEFINITION));


    public Classifier getClassifier(int classifierId) {
        Classifier classifier;
        if (classifiers.containsKey(classifierId))
            return classifiers.get(classifierId);

        switch (classifierId) {
            case DOCTYPE_CLASSIFIER_ID:
                ClassifierProto docTypeClassifierProto = new ClassifierProto(DOCTYPE_CLASSIFIER_ID, DOCTYPE_IDS);
                classifier = new ClassifierImpl (classifierId, docTypeClassifierProto, modelFactory, new DocTypeModelRVSetting(docTypeClassifierProto.getCategoryIds()));
                break;
            case TEN_K_TOC_CLASSIFIER_ID: case TEN_Q_TOC_CLASSIFIER_ID:case INDENTURE_TOC_CLASSIFIER_ID:case UNIVERSAL_TOC_CLASSIFIER_ID:case S4_TOC_CLASSIFIER_ID:
                ClassifierProto tocClassifierProto = new ClassifierProto(classifierId, TOC_CATEGORY_IDS);
                classifier = new ClassifierImpl(classifierId, tocClassifierProto, modelFactory, new TOCModelRVSetting(tocClassifierProto.getCategoryIds(), TOC_ANNOTATING_THRESHOLD, LOWER_TOC_CATEGORY_IDS, LOWER_TOC_ANNOTATING_THRESHOLD));
                break;
            case TEN_K_DEF_CLASSIFIER_ID:case TEN_Q_DEF_CLASSIFIER_ID:case INDENTURE_DEF_CLASSIFIER_ID:case UNIVERSAL_DEF_CLASSIFIER_ID:case S4_DEF_CLASSIFIER_ID:
                ClassifierProto defClassifierProto = new ClassifierProto(classifierId, DEF_CATEGORY_IDS);
                classifier = new ClassifierImpl (classifierId, defClassifierProto, modelFactory, new DefModelRVSetting(defClassifierProto.getCategoryIds()));
                break;
            default:
                throw new RuntimeException("Classifier Id: "+ classifierId + " is not supported");
        }

        classifiers.put(classifierId, classifier);
        return classifier;
    }

    public List<Classifier> getClassifiers(ClassifierFactoryStrategy classifierFactoryStrategy, Document document) throws Exception {
        return classifierFactoryStrategy.getClassifierIds(document)
                .stream()
                .map(classifierId -> this.getClassifier(classifierId))
                .collect(Collectors.toList());
    }
}
