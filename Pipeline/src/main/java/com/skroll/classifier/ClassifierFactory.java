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

    private static HashMap<ClassifierId, Classifier> classifiers = new HashMap();
    @Inject
    private ModelFactory modelFactory;

    //DocType Classifier
    private static final List<Integer> DOCTYPE_IDS = new ArrayList<>(Category.getDocType());
    //TOC Classifier
    private static final List<Integer> TOC_CATEGORY_IDS = new ArrayList<>(Arrays.asList(Category.NONE, Category.TOC_1, Category.TOC_2));
    //Def Classifier
    private static final List<Integer> DEF_CATEGORY_IDS = new ArrayList<>(Arrays.asList(Category.NONE, Category.DEFINITION));


    public Classifier getClassifier(ClassifierId classifierId) {
        Classifier classifier;
        if (classifiers.containsKey(classifierId))
            return classifiers.get(classifierId);

        switch (classifierId) {
            case DOCTYPE_CLASSIFIER:
                ClassifierProto docTypeClassifierProto = new ClassifierProto(classifierId, DOCTYPE_IDS);
                classifier = new ClassifierImpl (classifierId, docTypeClassifierProto, modelFactory, new DocTypeModelRVSetting(docTypeClassifierProto.getCategoryIds()));
                break;
            case TEN_K_TOC_CLASSIFIER: case TEN_Q_TOC_CLASSIFIER:case INDENTURE_TOC_CLASSIFIER:case UNIVERSAL_TOC_CLASSIFIER:
                ClassifierProto tocClassifierProto = new ClassifierProto(classifierId, TOC_CATEGORY_IDS);
                classifier = new ClassifierImpl(classifierId, tocClassifierProto, modelFactory, new TOCModelRVSetting(tocClassifierProto.getCategoryIds()));
                break;
            case TEN_K_DEF_CLASSIFIER:case TEN_Q_DEF_CLASSIFIER:case INDENTURE_DEF_CLASSIFIER:case UNIVERSAL_DEF_CLASSIFIER:
                ClassifierProto defClassifierProto = new ClassifierProto(classifierId, DEF_CATEGORY_IDS);
                classifier = new ClassifierImpl (classifierId, defClassifierProto, modelFactory, new DefModelRVSetting(defClassifierProto.getCategoryIds()));
                break;
            default:
                throw new RuntimeException("Classifier Id: "+ classifierId.getName() + " is not supported");
        }
        classifiers.put(classifierId, classifier);
        return classifier;
    }

    public List<Classifier> getClassifiers(ClassifierFactoryStrategy classifierFactoryStrategy, Document document) throws Exception {
        return classifierFactoryStrategy.getClassifierIds(document)
                .stream()
                .map(classifierEnum -> this.getClassifier(classifierEnum))
                .collect(Collectors.toList());
    }
}
