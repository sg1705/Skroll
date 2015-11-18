package com.skroll.document;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.classifier.DefaultClassifierFactoryStrategy;
import com.skroll.classifier.factory.CorpusFSModelFactoryImpl;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.trainer.CategoryTrainer;
import com.skroll.trainer.TrainerConfiguration;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * Created by wei on 11/17/15.
 */
public class ConvertDocToText {

    public static final Logger logger = LoggerFactory
            .getLogger(ConvertDocToText.class);

    public static DocumentFactory corpusDocumentFactory;
    static final String INPUT_FOLDER = "build/resources/main/preEvaluated/";
    public static void convertDocsToText () throws Exception {

        try {

            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(DocumentFactory.class)
                            .to(CorpusFSDocumentFactoryImpl.class);
                }
            });
            corpusDocumentFactory = injector.getInstance(DocumentFactory.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(INPUT_FOLDER));
        for (File f : iterable) {
            if (f.isFile()) {
                Document doc = corpusDocumentFactory.get(f.getName());
                //iterate over each paragraph
                if (doc == null) {
                    logger.error("Document can't be parsed. failed to train the model");
                    return;
                }




                for (CoreMap paragraph : doc.getParagraphs()) {
                    System.out.println(paragraph.getText());
                }
                try {
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] argvs) throws Exception{
        convertDocsToText();
    }

}
