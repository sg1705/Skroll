package com.skroll.util;

import com.google.inject.AbstractModule;
import com.skroll.analyzer.model.topic.RelatedParaWithInDocFinder;
import com.skroll.analyzer.model.topic.SkrollTopicModel;
import com.skroll.classifier.*;
import com.skroll.classifier.factory.*;
import com.skroll.document.factory.*;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.PhantomJsExtractor;
import com.skroll.services.mail.MailService;
import com.skroll.services.mail.SendGridMailService;
import com.skroll.services.mail.SendGridMailServiceImpl;

/**
 * Created by saurabhagarwal on 4/26/15.
 */
public class SkrollGuiceModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(Configuration.class);
        bind(ClassifierFactory.class);
        bind(PhantomJsExtractor.class);
        bind(ClassifierFactoryStrategy.class).annotatedWith(ClassifierFactoryStrategyForClassify.class).to(DefaultClassifierFactoryStrategyForClassify.class);
        bind(ClassifierFactoryStrategy.class).annotatedWith(ClassifierFactoryStrategyForTraining.class).to(DefaultClassifierFactoryStrategyForTraining.class);
        bind(ClassifierFactoryStrategy.class).annotatedWith(ClassifierFactoryStrategyForDocType.class).to(DocTypeClassifierFactoryStrategy.class);

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

        bind(MailService.class)
                .annotatedWith(SendGridMailService.class)
                .to(SendGridMailServiceImpl.class);

        bind(SkrollTopicModel.class).asEagerSingleton();

        bind(Parser.class);

    }
}
