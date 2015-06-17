package com.skroll.rest;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ModelFactory;
import com.skroll.document.factory.*;
import com.skroll.util.Configuration;
import com.skroll.util.TestConfiguration;

/**
 * Created by saurabh on 6/16/15.
 */
public class APITestGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ModelFactory.class);
        bind(ClassifierFactory.class);

        //default binding
        bind(DocumentFactory.class)
                .to(CorpusFSDocumentFactoryImpl.class);


        bind(DocumentFactory.class)
                .annotatedWith(Names.named("CorpusFSDocumentFactory"))
                .to(CorpusFSDocumentFactoryImpl.class);


        bind(DocumentFactory.class)
                .annotatedWith(Names.named("BenchmarkFSDocumentFactory"))
                .to(BenchmarkFSDocumentFactoryImpl.class);

        bind(Configuration.class).to(TestConfiguration.class);
    }

}
