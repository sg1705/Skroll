package com.skroll.document;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.skroll.classifier.Category;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class JsonDeserializerTest {
    public static final Logger logger = LoggerFactory
            .getLogger(Paragraph.class);

    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testGetJson() throws Exception {
        Document doc = createDoc();
        String jsonDoc = JsonDeserializer.getJson(doc);
        logger.debug("JSON:" + JsonDeserializer.getJson(doc));
        assert(jsonDoc.contains("jack"));
    }


    @Test
    public void testFromJson() throws Exception {

        String jsonDoc = JsonDeserializer.getJson(createDoc());
        logger.debug("JSON:" +jsonDoc);
        Document document = JsonDeserializer.fromJson(jsonDoc);
        logger.debug("Doc:" + document);
        CategoryAnnotationHelper.displayCategoryOfDoc(document);
        for (CoreMap paragraph : document.getParagraphs()) {
            logger.debug("paragraph:" +paragraph.getText());
            if (CategoryAnnotationHelper.isCategoryId(paragraph, Category.DEFINITION) ){
                List<List<String>> definitionList = CategoryAnnotationHelper.getDefinedTermLists(
                        paragraph, Category.DEFINITION);
                logger.debug("definitionList:" +Joiner.on(" ").join(definitionList));
                assert ((Joiner.on(" ").join(definitionList).contains("susan")));
            }

            List<Float> trainingWeight = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
            logger.debug("trainingWeight:" +trainingWeight);
           // assert((trainingWeight.get(0).floatValue()==1.0));
            logger.debug("JSON:" + JsonDeserializer.getJson(document));
        }
        assert (document.getSource().equals("source"));
        assert (document.getId() != null);
        assert (document.getId().equals("101"));
        assert (document.getTarget().equals("target"));
    }

    private Document createDoc() {
        Document doc = new Document();
        doc.setTarget("target");
        doc.setId("101");
        doc.setSource("source");
        List<CoreMap> paralist = new ArrayList<>();
        CoreMap paragraph =new CoreMap("1", "para");

        List<String> addedDefinition = Lists.newArrayList("jack", "susan");
        List<Token> tokens = DocumentHelper.getTokens(addedDefinition);
        CategoryAnnotationHelper.addDefinedTokensInCategoryAnnotation( paragraph, tokens, Category.DEFINITION);
        //paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        paragraph.set(CoreAnnotations.ParagraphIdAnnotation.class, "1");
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
        CategoryAnnotationHelper.addDefinedTokensInCategoryAnnotation(paragraph, tokens, Category.TOC_1);
        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.DEFINITION, (float) 1.0);
        TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.TOC_1, (float)0.5);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paralist.add(paragraph);

        List<List<String>> definitionList = CategoryAnnotationHelper.getDefinedTermLists(
                paragraph,Category.DEFINITION );
        for (List<String> definition : definitionList) {
            logger.debug(paragraph.getId() + "\t" + " annotation:" + "\t" + definition);

        }

        doc.setParagraphs(paralist);
        return doc;
    }
}