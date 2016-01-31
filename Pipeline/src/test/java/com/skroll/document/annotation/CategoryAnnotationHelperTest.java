package com.skroll.document.annotation;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.skroll.BaseTest;
import com.skroll.classifier.Category;
import com.skroll.document.*;
import com.skroll.parser.Parser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.fail;

public class CategoryAnnotationHelperTest extends BaseTest {

    private Document document;
    public static final Logger logger = LoggerFactory
            .getLogger(CategoryAnnotationHelperTest.class);
    @Before
    public void setUp() throws Exception {
        document = parser.parseDocumentFromHtmlFile("src/test/resources/classifier/miniture-indenture.html");

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetTerm() throws Exception {
        testSetMatchedText();
        logger.info("GetTerm:{}", CategoryAnnotationHelper.getParagraphsAnnotatedWithAnyCategory(document));
        assert(!CategoryAnnotationHelper.getParagraphsAnnotatedWithAnyCategory(document).isEmpty());
    }

    @Test
    public void testGetParaWithCategoryAnnotation() throws Exception {
        testSetMatchedText();
        logger.info("GetTerm:{}", CategoryAnnotationHelper.getParagraphsAnnotatedWithAnyCategory(document));
        assert(!CategoryAnnotationHelper.getParagraphsAnnotatedWithCategory(document, Category.DEFINITION).isEmpty());
    }


    @Test
    public void testAddTokensListInCategoryAnnotation() throws Exception {
        for(CoreMap paragraph: document.getParagraphs()){
            int categoryId = Category.DEFINITION;
            CategoryAnnotationHelper.annotateParagraphWithTokensAndCategory(paragraph, DocumentHelper.getTokens(Lists.newArrayList("becontinuing", ",")), Category.DEFINITION);
            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(paragraph, categoryId);
                logger.info(paragraph.getId() + "\t" + "existing definition:" + "\t" + Joiner.on(" , ").join(definitionList));
                assert(Joiner.on("").join(definitionList.get(0)).equals("becontinuing,"));
            }

        }
    }

    @Test
    public void testSetDefinedTermTokenListInParagraph() throws Exception {
      List<Token> tokens = Lists.newArrayList(new Token("jack"));
      List<List<Token>> tokensList =  Lists.newArrayList();
        tokensList.add(tokens);
        for(CoreMap paragraph: document.getParagraphs()) {
            CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategory(paragraph, tokensList, Category.DEFINITION);

        }
        assert(!CategoryAnnotationHelper.getParagraphsAnnotatedWithCategory(document, Category.DEFINITION).isEmpty());
    }

    @Test
    public void testClearAnnotations() throws Exception {
        testSetMatchedText();
        for(CoreMap coreMap: document.getParagraphs()) {
            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(coreMap, Category.DEFINITION)) {
                CategoryAnnotationHelper.clearCategoryAnnotations(coreMap);
                if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(coreMap, Category.DEFINITION)) {
                    fail(" failed to clear the annotations");
                }
            }
        }
    }

    @Test
    public void testSetMatchedText() throws Exception {
        for(CoreMap paragraph: document.getParagraphs()){
            int categoryId = Category.DEFINITION;
            CategoryAnnotationHelper.setMatchedText(paragraph, "continuing", categoryId);
            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(paragraph, categoryId);
                logger.info("{} \t definition: \t {}", paragraph.getId(), Joiner.on(" , ").join(definitionList));
                assert(Joiner.on("").join(definitionList).contains("continuing"));
            }
            CategoryAnnotationHelper.setMatchedText(paragraph, "Event", categoryId);
            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(paragraph, categoryId);
                logger.info("{} \t definition: \t {}", paragraph.getId(), Joiner.on(" , ").join(definitionList));
                assert(Joiner.on("").join(definitionList).contains("Event"));
            }

            categoryId = Category.TOC_1;
            CategoryAnnotationHelper.setMatchedText(paragraph, "continuing", categoryId);
            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(paragraph, categoryId);
                logger.info("{} \t toc: \t {}", paragraph.getId(), Joiner.on(" , ").join(definitionList));
                assert(Joiner.on("").join(definitionList).contains("continuing"));
            }
            CategoryAnnotationHelper.setMatchedText(paragraph, "Event", categoryId);
            if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) {
                List<List<String>> definitionList = CategoryAnnotationHelper.getTokenStringsForCategory(paragraph, categoryId);
                logger.info("{} \t toc: \t {}", paragraph.getId(), Joiner.on(" , ").join(definitionList));
                assert(Joiner.on("").join(definitionList).contains("Event"));
            }
        }
    }
}
