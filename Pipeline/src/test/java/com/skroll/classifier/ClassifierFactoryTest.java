package com.skroll.classifier;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ClassifierFactoryTest {

    ClassifierFactory classifierFactory = null;
    ClassifierFactoryStrategy classifierFactoryStrategy = null;
    @Before
    public void setup(){
        try {
            Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
            classifierFactory = injector.getInstance(ClassifierFactory.class);
            classifierFactoryStrategy = injector.getInstance(ClassifierFactoryStrategy.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testGetClassifiers() throws Exception {
        List<Classifier> classifiers = classifierFactory.getClassifiers(classifierFactoryStrategy);
        System.out.println("ClassifierFactory.getClassifiers(): " + classifiers);
        assert (classifiers.contains(classifierFactory.getClassifier(ClassifierFactory.DEF_CLASSIFIER_ID)));
    }

    @Test
    public void testGetClassifier() throws Exception {
        assert (classifierFactory.getClassifier(ClassifierFactory.DEF_CLASSIFIER_ID).getId() == ClassifierFactory.DEF_CLASSIFIER_ID);
    }

    @Test(expected = RuntimeException.class)
    public void testGetClassifierException() throws Exception {
        System.out.println("ClassifierFactory.getClassifier(): " + classifierFactory.getClassifier(-1));
    }
}