package com.skroll.document;

import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ModelHelperTest {


    public void testHtmlModel() {
        String fileName = "src/test/resources/document/experiment-jsoup-node-extraction.html";
        try {
            String htmlText = Utils.readStringFromFile(fileName);
            Document doc = new Document();
            doc.setTarget(htmlText);
            String jsonString = ModelHelper.getJson(doc);
            Document doc2 = ModelHelper.getModel(jsonString);
            String newHtmlText = doc2.getTarget();
            assert (htmlText.equals(newHtmlText));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void testHtmlModelParagraphs() {

        String fileName = "src/test/resources/document/experiment-jsoup-node-extraction.html";
        try {
            String htmlText = Utils.readStringFromFile(fileName);
            Document doc = new Document();

            List<String> input = new ArrayList<String>();
            input.add(htmlText);

            //create a pipeline
            Pipeline<List<String>, List<String>> documentChunkingPipeline =
                    new Pipeline.Builder<List<String>, List<String>>()
                            .add(Pipes.PARAGRAPH_CHUNKER)
                            .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                            .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                            .build();

            List<String> paragraphs = documentChunkingPipeline.process(input);

            // create fake paragraphs
            int ii = 0;
            List<CoreMap> paras = new ArrayList<CoreMap>();
            for (String para :  paragraphs) {
                CoreMap parag = new CoreMap(""+ii, para);
                paras.add(parag);
            }

            doc.setParagraphs(paras);


            String jsonString = ModelHelper.getJson(doc);
            Document doc2 = ModelHelper.getModel(jsonString);
            List<CoreMap> newParas = doc2.getParagraphs();
            System.out.println(newParas.size());
            assert (newParas.size() == paras.size());
            assert (newParas.size() != 0);


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void testModelHelperWithAnnotations() throws Exception {
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
        System.out.println(jsonString);

        assert (newParas.size() == paragraphs.size());
        assert (newParas.size() != 0);
    }



    public void testModelHelperWithAnnotationsWithSimpleDocument() throws Exception {
        String fileName = "src/test/resources/document/simple-html-text.html";
        Document doc = Parser.parseDocumentFromHtmlFile(fileName);

        List<CoreMap> paragraphs = doc.getParagraphs();

        // now get a Json string of the document
        String jsonString = ModelHelper.getJson(doc);
        Document doc2 = ModelHelper.getModel(jsonString);
        List<CoreMap> newParas = doc2.getParagraphs();
        System.out.println(newParas.size());
        System.out.println(jsonString);

        assert (newParas.size() == paragraphs.size());
        assert (newParas.size() != 0);
    }


}