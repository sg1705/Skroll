package com.skroll.trainer;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.factory.CorpusFSModelFactoryImpl;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.IOException;


/**
 * Created by saurabhagarwal on 1/19/15.
 */


/* current arguments for testing:
--trainWithOverride src/main/resources/trainingDocuments/indentures
--classify src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html
*/

public class SkrollTrainer extends Trainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(SkrollTrainer.class);

    @Inject
    public SkrollTrainer() {
        try {

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(DocumentFactory.class)
                        .to(CorpusFSDocumentFactoryImpl.class);
                bind(ModelFactory.class)
                        .to(CorpusFSModelFactoryImpl.class);
                bind(Configuration.class).to(TrainerConfiguration.class);
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
    public static void main(String[] args) throws IOException, ObjectPersistUtil.ObjectPersistException, Exception {

        SkrollTrainer skrollTrainer = new SkrollTrainer();

        //ToDO: use the apache common commandline
        if (args!= null && args.length >1) {
            if (args[0].equals("--trainWithWeight")) {
                logger.debug("folder Name :" + args[1]);
                skrollTrainer.trainFolderUsingTrainingWeight(args[1]);
            }
        } else {
            skrollTrainer.trainFolderUsingTrainingWeight(skrollTrainer.PRE_EVALUATED_FOLDER);
        }
    }
}
