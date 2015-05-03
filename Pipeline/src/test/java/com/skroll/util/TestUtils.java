package com.skroll.util;

import com.google.common.collect.Lists;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabhagarwal on 4/28/15.
 */
public class TestUtils {
    public static final Logger logger = LoggerFactory
            .getLogger(TestUtils.class);
    public static Document createDoc() {
        Document doc = new Document();
        doc.setTarget(" ");
        doc.setSource(" ");
        List<CoreMap> paralist = new ArrayList<>();
        CoreMap paragraph =new CoreMap("1253", "para");

        List<String> addedDefinition = Lists.newArrayList("jack", "susan");
        List<Token> tokens = DocumentHelper.getTokens(addedDefinition);
        DocumentHelper.addDefinedTermTokensInParagraph(tokens, paragraph);
        paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
        paragraph.set(CoreAnnotations.ParagraphIdAnnotation.class, "1");
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
        paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paralist.add(paragraph);
        List<List<String>> definitionList = DocumentHelper.getDefinedTermLists(
                paragraph);
        for (List<String> definition : definitionList) {
            logger.debug(paragraph.getId() + "\t" + " annotation:" + "\t" + definition);

        }


        CoreMap paragraph1 =new CoreMap("1253", "para");

        List<String> addedDefinition1 = Lists.newArrayList("sam", "adam");
        List<Token> tokens1 = DocumentHelper.getTokens(addedDefinition1);
        DocumentHelper.addTOCsInParagraph(tokens1, paragraph1);
        paragraph1.set(CoreAnnotations.IsTOCAnnotation.class, true);
        paragraph1.set(CoreAnnotations.ParagraphIdAnnotation.class, "1");
        paragraph1.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        paragraph1.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
        paralist.add(paragraph1);

        List<List<String>> definitionList1 = DocumentHelper.getDefinedTermLists(
                paragraph1);
        for (List<String> definition : definitionList1) {
            logger.debug(paragraph.getId() + "\t" + " annotation:" + "\t" + definition);

        }

        doc.setParagraphs(paralist);
        return doc;
    }
}
