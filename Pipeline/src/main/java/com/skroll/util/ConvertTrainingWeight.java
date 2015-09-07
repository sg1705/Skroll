package com.skroll.util;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.Category;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.classifier.DefaultClassifierFactoryStrategy;
import com.skroll.classifier.factory.CorpusFSModelFactoryImpl;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.trainer.TrainerConfiguration;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabhagarwal on 7/31/15.
 */
public class ConvertTrainingWeight {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(ConvertTrainingWeight.class);

    public ClassifierFactory classifierFactory;
    public Configuration configuration;
    public DocumentFactory documentFactory;
    public String PRE_EVALUATED_FOLDER;

    @Inject
    public ConvertTrainingWeight(String corpusLocation) {
        try {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bindConstant().annotatedWith(TrainerConfiguration.Location.class).to(corpusLocation);
                    bind(DocumentFactory.class)
                            .to(CorpusFSDocumentFactoryImpl.class);
                    bind(ModelFactory.class)
                            .to(CorpusFSModelFactoryImpl.class);
                    bind(Configuration.class).to(TrainerConfiguration.class);
                    bind(ClassifierFactory.class);
                    bind(ClassifierFactoryStrategy.class).to(DefaultClassifierFactoryStrategy.class);
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

    Options options = new Options();
    options.addOption("f", "folder", true, "provide the location of folder  to train or classify the documents");

    CommandLineParser parser=new GnuParser();
    CommandLine cmd=parser.parse(options,args);

    String folder = cmd.getOptionValue("folder");
    if(folder == null){
        folder = "build/resources/main/preEvaluated/";
    }

    logger.info("Folder name {}", cmd.getOptionValue("folder"));
    ConvertTrainingWeight convertTrainingWeight = new ConvertTrainingWeight(folder);
        convertTrainingWeight.convertTrainingWeightAnnotationIntoCategoryWeight(folder);


}
    public void convertTrainingWeightAnnotationIntoCategoryWeight(String fileName) {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(fileName));
        List<String> docLists = new ArrayList<String>();
        int counter = 0;
        for (File f : iterable) {
            if (f.isFile()) {
                try {
                    Document document = documentFactory.get(f.getName());
                    convertTrainingWeightAnnotationIntoCategoryWeight(document);
                    documentFactory.saveDocument(document);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Convert the trainingWeightAnnotation into category Weight. Used for migration.
     * @param document
     */
    public void convertTrainingWeightAnnotationIntoCategoryWeight(Document document){
        int LATEST_WEIGHT_INDEX =7;
        for (CoreMap paragraph : document.getParagraphs()) {
            HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
            List<Float>  weightList = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
            if (weightList == null)
                continue;
            if(categoryAnnotation == null && weightList != null ) {
                CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.NONE, weightList.get(Category.NONE + LATEST_WEIGHT_INDEX), weightList.get(Category.NONE));
                logger.trace("convert paragraph Id {} for categoryId {} for weight {}", paragraph.getId(), Category.NONE, weightList.get(Category.NONE + LATEST_WEIGHT_INDEX));
            }
            if (categoryAnnotation != null) {
                for (int categoryId : Category.getCategories()) {
                    CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
                    if (annotationCoreMap != null) {
                        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, categoryId, weightList.get(categoryId + LATEST_WEIGHT_INDEX), weightList.get(categoryId));
                        logger.trace("convert paragraph Id {} for categoryId {} for weight {}", paragraph.getId(), categoryId, weightList.get(categoryId + LATEST_WEIGHT_INDEX));
                    }
                }
            }
            paragraph.set(CoreAnnotations.TrainingWeightAnnotationFloat.class,null);
        }
    }
}
