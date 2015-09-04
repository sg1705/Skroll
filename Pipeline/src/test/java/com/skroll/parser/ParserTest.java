package com.skroll.parser;

import com.google.common.collect.Lists;
import com.skroll.classifier.Category;
import com.skroll.document.Document;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Utils;
import org.junit.Test;

import java.util.List;

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

        CategoryAnnotationHelper.annotateCategoryWeight(htmlDoc.getParagraphs().get(1), Category.DEFINITION, 1.0f);
        //remove version
        htmlDoc.set(CoreAnnotations.ParserVersionAnnotationInteger.class, 0);
        Document doc = Parser.reParse(htmlDoc);
        doc.setId(fileName);
        System.out.println("New Version:" + doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
        assert (doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class) == Parser.VERSION);
        assert (doc.getParagraphs().get(0).get(CoreAnnotations.IsUserObservationAnnotation.class));
        assert (doc.equals(htmlDoc));
    }

    @Test
    public void testParseDocumentFromUrl() throws Exception {
        String url = "http://www.sec.gov/Archives/edgar/data/1418091/000095012314003031/twtr-10k_20131231.htm";
        String fileName = "aa";
        Document htmlDoc = Parser.parseDocumentFromUrl(url);
        htmlDoc.setId(fileName);
        htmlDoc.getParagraphs().get(0).set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        CategoryAnnotationHelper.annotateCategoryWeight(htmlDoc.getParagraphs().get(1), Category.DEFINITION, 1.0f);
        //remove version
        htmlDoc.set(CoreAnnotations.ParserVersionAnnotationInteger.class, 0);
        Document doc = Parser.reParse(htmlDoc);
        doc.setId(fileName);
        System.out.println("New Version:" + doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
        assert (doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class) == Parser.VERSION);
        assert (doc.getParagraphs().get(0).get(CoreAnnotations.IsUserObservationAnnotation.class));
        assert (doc.getParagraphs().size() != 0);
        assert (doc.get(CoreAnnotations.SourceUrlAnnotation.class).equals(url));
        assert (doc.equals(htmlDoc));
    }

    @Test
    public void testParsePartialDocumentFromUrl() throws Exception {
        String url = "http://www.sec.gov/Archives/edgar/data/1418091/000095012314003031/twtr-10k_20131231.htm";
        String fileName = "aa";
        Document htmlDoc = Parser.parsePartialDocumentFromUrl(url);
        htmlDoc.setId(fileName);
        assert (htmlDoc.get(CoreAnnotations.SourceUrlAnnotation.class).equals(url));
        assert (htmlDoc.getParagraphs().size() == 0);
    }
}