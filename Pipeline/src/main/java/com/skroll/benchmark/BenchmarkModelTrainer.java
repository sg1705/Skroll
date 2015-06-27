package com.skroll.benchmark;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.factory.BenchmarkFSModelFactoryImpl;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.trainer.Trainer;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;


/**
 * Created by saurabhagarwal on 1/19/15.
 */


/* current arguments for testing:
--trainWithOverride src/main/resources/trainingDocuments/indentures
--classify src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html
*/

public class BenchmarkModelTrainer extends Trainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(BenchmarkModelTrainer.class);

    @Inject
    public BenchmarkModelTrainer() {
        try {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DocumentFactory.class)
                        .to(CorpusFSDocumentFactoryImpl.class);
                bind(ModelFactory.class)
                        .to(BenchmarkFSModelFactoryImpl.class);
                bind(Configuration.class);
                bind(ClassifierFactory.class);
            }
        });
        classifierFactory = injector.getInstance(ClassifierFactory.class);
        documentFactory = injector.getInstance(DocumentFactory.class);
        configuration = injector.getInstance(Configuration.class);
        PRE_EVALUATED_FOLDER = configuration.get("preEvaluatedFolder", "/tmp/");
    } catch (Exception e) {
        e.printStackTrace();
    }
    }
    public static void main(String[] args) throws Exception {

        BenchmarkModelTrainer skrollTrainer = new BenchmarkModelTrainer();

        //ToDO: use the apache common commandline
        if (args!= null && args.length >1) {
            if (args[0].equals("--trainBenchmarkModelWithWeight")) {
                logger.debug("folder Name :" + args[1]);
                skrollTrainer.trainFolderUsingTrainingWeight(args[1]);
            }
        } else {
            skrollTrainer.trainFolderUsingTrainingWeight(skrollTrainer.PRE_EVALUATED_FOLDER);
        }
    }
}