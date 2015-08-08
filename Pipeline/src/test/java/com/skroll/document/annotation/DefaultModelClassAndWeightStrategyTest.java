package com.skroll.document.annotation;

import com.google.common.collect.Lists;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class DefaultModelClassAndWeightStrategyTest {

    ManagedCategoryStrategy managedCategoryStrategy = new DefaultManagedCategoryStrategy();
    UnManagedCategoryStrategy unManagedCategoryStrategy = new DefaultUnManagedCategoryStrategy();
    ModelClassAndWeightStrategy modelClassAndWeightStrategy = new DefaultModelClassAndWeightStrategy(managedCategoryStrategy, unManagedCategoryStrategy);
    CoreMap paragraph;
    public static final Logger logger = LoggerFactory
            .getLogger(DefaultModelClassAndWeightStrategyTest.class);

    @Before
    public void setUp() throws Exception {
        Document doc = Parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");

        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);

        // one paragraphs are User train
        paragraph = doc.getParagraphs().get(0);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);

        /*
        paragraph = doc.getParagraphs().get(1);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);

        paragraph = doc.getParagraphs().get(2);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.TOC_1, 1);
        CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);
        */

    }

    @Test
    public void testGetClassIndexForModelUsingUnManagedCategory() throws Exception {
        int classIndexUsingUnManagedCategory = modelClassAndWeightStrategy.getClassIndexForModel(paragraph, Arrays.asList(Category.NONE, Category.DEFINITION));
        System.out.println("ClassIndexUsingUnManagedCategory:" + classIndexUsingUnManagedCategory);

    }

    @Test
    public void testGetCategoryIdForModel() throws Exception {
        int classIndexUsingUnManagedCategory = modelClassAndWeightStrategy.getClassIndexForModel(paragraph, Arrays.asList(Category.NONE, Category.DEFINITION));
        System.out.println("ClassIndexUsingUnManagedCategory:" + classIndexUsingUnManagedCategory);

    }

    @Test
    public void testGetObservedParagraphs() throws Exception {

    }

    @Test
    public void testPopulateTrainingWeightsUsingUnManagedCategory() throws Exception {
        double[][] trainingWeights = modelClassAndWeightStrategy.populateTrainingWeights(paragraph, Arrays.asList(Category.NONE, Category.DEFINITION, Category.USER_TOC));
        if (logger.isDebugEnabled()) {
            for (double[] w1 : trainingWeights) {
                for (double w2 : w1)
                    logger.debug("trainingWeights: {}", w2);
            }
        }

    }
}