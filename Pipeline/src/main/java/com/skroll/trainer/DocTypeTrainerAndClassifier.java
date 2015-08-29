package com.skroll.trainer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.classifier.DocTypeClassifierFactoryStrategy;
import com.skroll.classifier.factory.CorpusFSModelFactoryImpl;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.document.factory.SingleParaDocumentFactoryImpl;
import com.skroll.document.factory.SingleParaFSDocumentFactory;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Created by saurabhagarwal on 1/19/15.
 */


/* current arguments for testing:
--trainWithOverride src/main/resources/trainingDocuments/indentures
--classify src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html
*/

public class DocTypeTrainerAndClassifier extends Trainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(DocTypeTrainerAndClassifier.class);

    public DocTypeTrainerAndClassifier() {
        try {

            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Configuration.class).to(TrainerConfiguration.class);
                    bind(DocumentFactory.class)
                            .to(CorpusFSDocumentFactoryImpl.class);
                    bind(ModelFactory.class)
                            .to(CorpusFSModelFactoryImpl.class);
                    bind(ClassifierFactory.class);
                    bind(ClassifierFactoryStrategy.class).to(DocTypeClassifierFactoryStrategy.class);
                    bind(DocumentFactory.class)
                            .annotatedWith(SingleParaFSDocumentFactory.class)
                            .to(SingleParaDocumentFactoryImpl.class);

                }
            });
            classifierFactory = injector.getInstance(ClassifierFactory.class);
            classifierFactoryStrategy = injector.getInstance(ClassifierFactoryStrategy.class);
            corpusDocumentFactory = injector.getInstance(DocumentFactory.class);
            singleParaDocumentFactory = injector.getInstance(Key.get(DocumentFactory.class, SingleParaFSDocumentFactory.class));
            configuration = injector.getInstance(Configuration.class);
            PRE_EVALUATED_FOLDER = configuration.get("preEvaluatedFolder", "/tmp/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}