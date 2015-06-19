package com.skroll.classifier;

import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.util.SkrollGuiceModule;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by saurabh on 6/14/15.
 */
public class ClassifierLogicTest {

    ClassifierFactory classifierFactory = null;

    @Before
    public void setup(){
        try {
            Injector injector = Guice.createInjector(new SkrollGuiceModule());
            classifierFactory = injector.getInstance(ClassifierFactory.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    public void testNoClassificationOfUserTrainedPara() throws Exception {
        assert(classifierFactory.getClassifier(Category.TOC_1)!= null);
        //create a new document
        Document doc = Parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");

        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);

        // one paragraphs as user trained
        CoreMap paragraph = doc.getParagraphs().get(0);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        // set training weight on that paragrpah
        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);
        // classify
        classifierFactory.getClassifier(Category.TOC_1).classify(doc.getId(),doc );

        assert (CategoryAnnotationHelper.isCategoryId(paragraph, Category.TOC_1));
        assert (!CategoryAnnotationHelper.isCategoryId(doc.getParagraphs().get(1), Category.TOC_1));

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
