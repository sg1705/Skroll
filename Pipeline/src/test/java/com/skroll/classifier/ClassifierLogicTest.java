package com.skroll.classifier;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
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
    ClassifierFactoryStrategy classifierFactoryStrategy = null;
    Configuration config;
    int categoryId = 2;
    String categoryName = "TestClassifier.model";

    @Before
    public void setup(){
        try {
            Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
            classifierFactory = injector.getInstance(ClassifierFactory.class);
            classifierFactoryStrategy = injector.getInstance(ClassifierFactoryStrategy.class);
            config = injector.getInstance(Configuration.class);
            String modelFolder = config.get("modelFolder");
            //delete an existing model
            File f = new File(modelFolder + "/" + categoryName);
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
        //create a new document
        Document doc = Parser.parseDocumentFromHtml("<div><u>This is a awesome</u></div>" +
                "<div><u>This is a awesome</u></div>" +
                "<div><u>This is a awesome</u></div");

        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);
        CategoryAnnotationHelper.clearCategoryAnnotations(doc.getParagraphs().get(1));
        // one paragraphs as user trained
        CoreMap paragraph = doc.getParagraphs().get(0);
        doc.setId("test");
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        // set training weight on that paragrpah
        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, this.categoryId, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), this.categoryId);
        // classify
        for (Classifier classifier : classifierFactory.getClassifiers(classifierFactoryStrategy, doc)) {
            classifier.classify(doc.getId(), doc);
        }
        //test to see if all paragraphs were assigned categories
        assert (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, this.categoryId));
        paragraph = doc.getParagraphs().get(1);
        assert (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, this.categoryId));
        paragraph = doc.getParagraphs().get(2);
        assert (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, this.categoryId));
        //now persist the file
        String json = JsonDeserializer.getJson(doc);
        Document newDoc = JsonDeserializer.fromJson(json);
        String newJson = JsonDeserializer.getJson(newDoc);
        //check to see if the new document is the same as the old document
        assert (doc.equals(newDoc));
        assert (newJson.equals(json));
        //test reparsing
        Document reParsed = Parser.reParse(newDoc);
        doc.setId("test");
        assert (newDoc.equals(reParsed));
        //test the model by creating a new model
    }

    @Test
    public void testAllUserObserved() throws Exception {
        //create a new document
        Document doc = Parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");

        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);

        // one paragraphs are User train
        CoreMap paragraph = doc.getParagraphs().get(0);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);

        paragraph = doc.getParagraphs().get(1);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);

        paragraph = doc.getParagraphs().get(2);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);


        // classify
        for (Classifier classifier :classifierFactory.getClassifiers(classifierFactoryStrategy,doc) ){

            classifier.classify(doc.getId(), doc);
        }
        assert (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, Category.TOC_1));
        assert (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(doc.getParagraphs().get(1), Category.TOC_1));
        assert (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(doc.getParagraphs().get(2), Category.TOC_1));

    }


}
