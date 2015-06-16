package com.skroll.util;

import com.google.inject.AbstractModule;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ModelFactory;
import com.skroll.document.factory.*;

/**
 * Created by saurabhagarwal on 4/26/15.
 */
public class SkrollTestGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ModelFactory.class);
        bind(ClassifierFactory.class);
        bind(TestConfiguration.class);

        //default binding
        bind(IDocumentFactory.class)
                .to(CorpusFSDocumentFactoryImpl.class);


        bind(IDocumentFactory.class)
                .annotatedWith(CorpusFSDocumentFactory.class)
                .to(CorpusFSDocumentFactoryImpl.class);

        bind(IDocumentFactory.class)
                .annotatedWith(BenchmarkFSDocumentFactory.class)
                .to(BenchmarkFSDocumentFactoryImpl.class);



    }
}