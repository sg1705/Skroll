package com.skroll.classifier;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.util.SkrollGuiceModule;
import org.junit.Before;
import org.junit.Test;

public class ClassifierFactoryTest {

    ClassifierFactory classifierFactory = null;
    @Before
    public void setup(){
        try {
            Injector injector = Guice.createInjector(new SkrollGuiceModule());
            classifierFactory = injector.getInstance(ClassifierFactory.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testGetClassifier() throws Exception {
        assert(classifierFactory.getClassifier(Category.DEFINITION)!=null);
        assert(classifierFactory.getClassifier(Category.TOC)!=null);

    }

}