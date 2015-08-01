package com.skroll.util;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MigrationUtilTest {
    private Document document;
    public static final Logger logger = LoggerFactory
            .getLogger(MigrationUtilTest.class);
    @Before
    public void setUp() throws Exception {
        document = Parser.parseDocumentFromHtmlFile("src/test/resources/classifier/smaller-indenture.html");

    }

    @Test
    public void testConvertTrainingWeightAnnotationIntoCategoryWeight() throws Exception {
        MigrationUtil migrationUtil = new MigrationUtil();
        CoreMap paragraph = document.getParagraphs().get(0);
        int categoryId = Category.DEFINITION;
        CategoryAnnotationHelper.annotateParagraphWithTokensAndCategory(paragraph, DocumentHelper.getTokens(Lists.newArrayList("becontinuing", ",")), Category.DEFINITION);
        if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) {
            List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(paragraph, categoryId);
            logger.info(paragraph.getId() + "\t" + "existing definition:" + "\t" + Joiner.on(" , ").join(definitionList));
            assert(Joiner.on("").join(definitionList.get(0)).equals("becontinuing,"));
        }
        List<Float>  weightList = Lists.newArrayList(0f,0f,0f,0f,0f,0f,0f,0f,1f,0f,0f,0f,0f,0f);
        paragraph.set(CoreAnnotations.TrainingWeightAnnotationFloat.class, weightList);
        migrationUtil.convertTrainingWeightAnnotationIntoCategoryWeight(document);
        CategoryAnnotationHelper.displayParagraphsAnnotatedWithAnyCategory(paragraph);
        CoreMap categoryAnnotation = CategoryAnnotationHelper.getCategoryAnnotation(paragraph, categoryId);
        logger.debug("PriorCategoryWeight: {} ",  categoryAnnotation.get(CoreAnnotations.PriorCategoryWeightFloat.class));
        logger.debug("CurrentCategoryWeight: {} ",  categoryAnnotation.get(CoreAnnotations.CurrentCategoryWeightFloat.class));
        assert(categoryAnnotation.get(CoreAnnotations.CurrentCategoryWeightFloat.class) == 1f);
        assert(paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class) == null);
    }
}