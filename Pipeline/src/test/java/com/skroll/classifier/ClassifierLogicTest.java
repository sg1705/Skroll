package com.skroll.classifier;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.analyzer.model.applicationModel.TOCModelRVSetting;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.util.Configuration;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;

/**
 * Created by saurabh on 6/14/15.
 */
public class ClassifierLogicTest {

    ClassifierFactory classifierFactory = null;
    Configuration config;
    int categoryId = 6;
    String categoryName = "TestClassifier.model";

    @Before
    public void setup(){
        try {
            Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
            classifierFactory = injector.getInstance(ClassifierFactory.class);
            config = injector.getInstance(Configuration.class);
            String modelFolder = config.get("modelFolder");
            //delete an existing model
            File f = new File(modelFolder+"/"+categoryName);
            try {
                Files.delete(f.toPath());
            } catch (NoSuchFileException e) {

            } catch (Exception e) {
                throw e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testNoClassificationOfUserTrainedPara() throws Exception {
        this.classifierFactory.createClassifier(new TOCModelRVSetting(this.categoryId, this.categoryName));
        assert(classifierFactory.getClassifier(this.categoryId)!= null);
        //create a new document
        Document doc = Parser.parseDocumentFromHtml("<div><u>This is a awesome</u></div>" +
                "<div>second paragraph</div>" +
                "<div>third paragraph</div");

        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);
        CategoryAnnotationHelper.clearAnnotations(doc.getParagraphs().get(1));
        // one paragraphs as user trained
        CoreMap paragraph = doc.getParagraphs().get(0);
        doc.setId("test");
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        // set training weight on that paragrpah
        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, this.categoryId, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), this.categoryId);
        // classify
        classifierFactory.getClassifier(this.categoryId).classify(doc.getId(),doc );
        //print categories for each para
        assert (CategoryAnnotationHelper.isCategoryId(paragraph, this.categoryId));
        paragraph = doc.getParagraphs().get(1);
        assert (CategoryAnnotationHelper.isCategoryId(paragraph, this.categoryId));
        paragraph = doc.getParagraphs().get(2);
        assert (CategoryAnnotationHelper.isCategoryId(paragraph, this.categoryId));
        //now persist the file
        String json = JsonDeserializer.getJson(doc);
        Document newDoc = JsonDeserializer.fromJson(json);
        String newJson = JsonDeserializer.getJson(newDoc);
        assert (doc.equals(newDoc));
        
        assert (newJson.equals(json));

    }

    @Test
    public void testAllUserObserved() throws Exception {
        assert(classifierFactory.getClassifier(Category.TOC_1)!= null);
        //create a new document
        Document doc = Parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");

        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);

        // one paragraphs are User train
        CoreMap paragraph = doc.getParagraphs().get(0);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);

        paragraph = doc.getParagraphs().get(1);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);

        paragraph = doc.getParagraphs().get(2);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);


        // classify
        classifierFactory.getClassifier(Category.TOC_1).classify(doc.getId(),doc );

        assert (CategoryAnnotationHelper.isCategoryId(paragraph, Category.TOC_1));
        assert (CategoryAnnotationHelper.isCategoryId(doc.getParagraphs().get(1), Category.TOC_1));
        assert (CategoryAnnotationHelper.isCategoryId(doc.getParagraphs().get(2), Category.TOC_1));

    }


}
