package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.PhantomJsExtractor;
import com.skroll.pipeline.util.Utils;
import org.junit.Test;
import java.util.List;

public class ModelHelperTest {


    @Test
    public void testModelHelperWithAnnotations() throws Exception {
//        PhantomJsExtractor.TEST_FLAGS = true;
        String fileName = "src/test/resources/document/experiment-jsoup-node-extraction.html";
        Document doc = Parser.parseDocumentFromHtmlFile(fileName);
        List<CoreMap> paragraphs = doc.getParagraphs();
        doc.setTarget("");
        doc.setSource("");
        // now get a Json string of the document
        String jsonString = ModelHelper.getJson(doc);
        Utils.writeToFile("/tmp/myjson.json", jsonString);
        Document doc2 = ModelHelper.getModel(jsonString);
        List<CoreMap> newParas = doc2.getParagraphs();
        System.out.println(newParas.size());
        System.out.println(doc2.get(CoreAnnotations.TablesAnnotation.class).size());
        int totalTables = doc2.get(CoreAnnotations.TablesAnnotation.class).size();

        assert( totalTables == 0);
        assert (newParas.size() == paragraphs.size());
        assert (newParas.size() != 0);
    }


    @Test
    public void testModelHelperWithAnnotationsWithSimpleDocument() throws Exception {
        String fileName = "src/test/resources/document/simple-html-text.html";
        Document doc = Parser.parseDocumentFromHtmlFile(fileName);

        List<CoreMap> paragraphs = doc.getParagraphs();

        // now get a Json string of the document
        String jsonString = ModelHelper.getJson(doc);
        Document doc2 = ModelHelper.getModel(jsonString);
        List<CoreMap> newParas = doc2.getParagraphs();
        System.out.println(newParas.size());

        assert (newParas.size() == paragraphs.size());
        assert (newParas.size() != 0);
    }


}