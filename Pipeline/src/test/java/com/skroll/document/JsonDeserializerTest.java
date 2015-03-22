package com.skroll.document;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
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
        logger.debug("Doc:" +document);
        for (CoreMap paragraph : document.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                        paragraph);
                logger.debug("definitionList:" +Joiner.on(" ").join(definitionList));
                assert((Joiner.on(" ").join(definitionList).contains("susan")));
            }
            List<Float> trainingWeight = paragraph.get(CoreAnnotations.TrainingWeightAnnotation.class);
            logger.debug("trainingWeight:" +trainingWeight);
           // assert((trainingWeight.get(0).floatValue()==1.0));
            logger.debug("JSON:" + JsonDeserializer.getJson(document));
        }
    }

    private Document createDoc() {
        Document doc = new Document();
        List<CoreMap> paralist = new ArrayList<>();
        CoreMap paragraph =new CoreMap("1", "para");

        List<String> addedDefinition = Lists.newArrayList("jack", "susan");
        List<Token> tokens = DocumentHelper.getTokens(addedDefinition);
        DocumentHelper.addDefinedTermTokensInParagraph(tokens, paragraph);
        paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        paragraph.set(CoreAnnotations.ParagraphIdAnnotation.class, "1");
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
        TrainingWeightAnnotationHelper.updateTrainingWeight(paragraph, TrainingWeightAnnotationHelper.DEFINITION, (float) 1.0);
        TrainingWeightAnnotationHelper.updateTrainingWeight(paragraph, TrainingWeightAnnotationHelper.TOC, (float)0.5);

        paralist.add(paragraph);

        List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                paragraph);
        for (List<String> definition : definitionList) {
            logger.debug(paragraph.getId() + "\t" + " annotation:" + "\t" + definition);

        }

        doc.setParagraphs(paralist);
        return doc;
    }
}