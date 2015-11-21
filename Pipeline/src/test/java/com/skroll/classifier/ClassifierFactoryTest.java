package com.skroll.classifier;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.skroll.document.Document;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ClassifierFactoryTest {

    ClassifierFactory classifierFactory = null;
    ClassifierFactoryStrategy classifierFactoryStrategyForClassify = null;
    Document doc = null;

    @Before
    public void setup(){
        try {
            Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
            classifierFactory = injector.getInstance(ClassifierFactory.class);
            classifierFactoryStrategyForClassify = injector.getInstance(Key.get(ClassifierFactoryStrategy.class, ClassifierFactoryStrategyForClassify.class));

            //create a new document
            doc = Parser.parseDocumentFromHtml("<div><u>This is a awesome</u></div>" +
                    "<div><u>This is a awesome</u></div>" +
                    "<div><u>This is a awesome</u></div");
            DocTypeAnnotationHelper.annotateDocTypeWithWeightAndUserObservation(doc,Category.TEN_K,1f);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testGetClassifiers() throws Exception {
        List<Classifier> classifiers = classifierFactory.getClassifiers(classifierFactoryStrategyForClassify, doc);
        System.out.println("ClassifierFactory.getClassifiers(): " + classifiers);
        assert (classifiers.contains(classifierFactory.getClassifier(ClassifierFactory.TEN_K_TOC_CLASSIFIER_ID)));
    }

    @Test
    public void testGetClassifier() throws Exception {
        assert(classifierFactory.getClassifier(ClassifierFactory.TEN_K_TOC_CLASSIFIER_ID).getId() == ClassifierFactory.TEN_K_TOC_CLASSIFIER_ID);

    }
}