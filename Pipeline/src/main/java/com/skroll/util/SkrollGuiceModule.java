package com.skroll.util;

import com.google.inject.AbstractModule;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.classifier.DefaultClassifierFactoryStrategy;
import com.skroll.classifier.factory.*;
import com.skroll.document.factory.*;
import com.skroll.parser.extractor.PhantomJsExtractor;

/**
 * Created by saurabhagarwal on 4/26/15.
 */
public class SkrollGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Configuration.class);
        bind(ClassifierFactory.class);
        bind(PhantomJsExtractor.class);
        bind(ClassifierFactoryStrategy.class).to(DefaultClassifierFactoryStrategy.class);

        //default binding for document factory
        bind(DocumentFactory.class)
                .to(CorpusFSDocumentFactoryImpl.class);

        bind(DocumentFactory.class)
                .annotatedWith(CorpusFSDocumentFactory.class)
                .to(CorpusFSDocumentFactoryImpl.class);

        bind(DocumentFactory.class)
                .annotatedWith(SingleParaFSDocumentFactory.class)
                .to(SingleParaDocumentFactoryImpl.class);

        bind(DocumentFactory.class)
                .annotatedWith(BenchmarkFSDocumentFactory.class)
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