package com.skroll.trainer;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.skroll.classifier.*;
import com.skroll.classifier.factory.CorpusFSModelFactoryImpl;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * CategoryTrainer injects right classes for training and classifying using the paragraphs of document for command line utility
 * Created by saurabhagarwal on 1/19/15.
 */

public class CategoryTrainer extends Trainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(CategoryTrainer.class);

    @Inject
    public CategoryTrainer(String corpusLocation) {
        try {

            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bindConstant().annotatedWith(TrainerConfiguration.Location.class).to(corpusLocation);
                    bind(Configuration.class).to(TrainerConfiguration.class);
                    bind(DocumentFactory.class)
                            .to(CorpusFSDocumentFactoryImpl.class);
                    bind(ModelFactory.class)
                            .to(CorpusFSModelFactoryImpl.class);
                    bind(ClassifierFactory.class);
                    bind(ClassifierFactoryStrategy.class).annotatedWith(ClassifierFactoryStrategyForTraining.class).to(DefaultClassifierFactoryStrategyForTraining.class);
                }
            });
            classifierFactory = injector.getInstance(ClassifierFactory.class);
            classifierFactoryStrategy = injector.getInstance(Key.get(ClassifierFactoryStrategy.class, ClassifierFactoryStrategyForTraining.class));
            corpusDocumentFactory = injector.getInstance(DocumentFactory.class);
            configuration = injector.getInstance(Configuration.class);
            PRE_EVALUATED_FOLDER = configuration.get("preEvaluatedFolder", "/tmp/");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void checkPreEvaluatedFile() {
        String fileName = "build/resources/main/preEvaluated/";
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(fileName));
        List<String> docLists = new ArrayList<String>();
        int counter = 0;
        for (File f : iterable) {
            if (f.isFile()) {
                try {
                    Document document = corpusDocumentFactory.get(f.getName());
                    for (CoreMap paragraph : document.getParagraphs()) {
                        for (int categoryId : Category.getCategoriesExcludingNONE()) {
                            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) {
                                List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(
                                        paragraph, categoryId);
                                //logger.debug("definitionList:" + Joiner.on(" ").join(definitionList));
                                if (definitionList == null) {
                                    logger.error("********** corrupted file ********* {}", f.getName());
                                    break;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}