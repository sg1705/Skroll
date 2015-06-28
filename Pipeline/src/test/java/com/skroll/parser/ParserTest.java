package com.skroll.parser;

import com.google.common.collect.Lists;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Utils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class ParserTest {

    @Test
    public void testReParse() throws Exception {
        //create a document
        String fileName = "src/test/resources/document/simple-html-text.html";
        String htmlString = Utils.readStringFromFile(fileName);
        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);
        htmlDoc.setId(fileName);
        htmlDoc.getParagraphs().get(0).set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        List<Float> weights = Lists.newArrayList(1.0f, 2.0f, 3.0f);
        htmlDoc.getParagraphs().get(1).set(CoreAnnotations.TrainingWeightAnnotationFloat.class, weights);
        //remove version
        htmlDoc.set(CoreAnnotations.ParserVersionAnnotationInteger.class, 0);
        Document doc = Parser.reParse(htmlDoc);
        doc.setId(fileName);
        System.out.println("New Version:" + doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
        assert (doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class) == Parser.VERSION);
        assert (doc.getParagraphs().get(0).get(CoreAnnotations.IsUserObservationAnnotation.class));
        assert (doc.getParagraphs().get(1).get(CoreAnnotations.TrainingWeightAnnotationFloat.class).equals(weights));
        assert (doc.equals(htmlDoc));
    }
}