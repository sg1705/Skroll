package com.skroll.util;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.factory.*;
import com.skroll.document.factory.*;

/**
 * Created by saurabh on 6/16/15.
 */
public class SkrollTestGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(Configuration.class).to(TestConfiguration.class);
        bind(ClassifierFactory.class);

        //default binding
        bind(DocumentFactory.class)
                .to(CorpusFSDocumentFactoryImpl.class);

        bind(DocumentFactory.class)
                .annotatedWith(CorpusFSDocumentFactory.class)
                .to(CorpusFSDocumentFactoryImpl.class);

        bind(DocumentFactory.class)
                .annotatedWith(Names.named("CorpusFSDocumentFactory"))
                .to(CorpusFSDocumentFactoryImpl.class);


        bind(DocumentFactory.class)
                .annotatedWith(BenchmarkFSDocumentFactory.class)
                .to(BenchmarkFSDocumentFactoryImpl.class);

        bind(DocumentFactory.class)
                .annotatedWith(Names.named("BenchmarkFSDocumentFactory"))
                .to(BenchmarkFSDocumentFactoryImpl.class);



        //default binding for model
        bind(ModelFactory.class)
                .to(CorpusFSModelFactoryImpl.class);

        bind(ModelFactory.class)
                .annotatedWith(CorpusFSModelFactory.class)
                .to(CorpusFSModelFactoryImpl.class);

        bind(ModelFactory.class)
                .annotatedWith(BenchmarkFSModelFactory.class)
                .to(BenchmarkFSModelFactoryImpl.class);

    }

}
