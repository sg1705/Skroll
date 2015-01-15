package com.skroll.document;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;

import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HtmlDocumentToFilePipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        String fileName = "src/test/resources/document/experiment-jsoup-node-extraction.html";
        String targetFile = "build/resources/test/generated/document/experiment-jsoup-node-extraction.html";
        String targetJsonFile = "build/resources/test/generated/document/documentModel.json";

        Files.createParentDirs(new File(targetFile));

        String htmlText = Utils.readStringFromFile(fileName);

        Document htmlDoc = new Document(htmlText);

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.SAVE_HTML_DOCUMENT_TO_FILE, Lists.newArrayList(targetFile))
                        .build();

        Document doc = pipeline.process(htmlDoc);

        // read the file and recreate the doc
        String newJsonText = Utils.readStringFromFile(targetFile);
        Document newDoc = ModelHelper.getModel(newJsonText);

        Utils.writeToFile(targetJsonFile, newJsonText);
        System.out.println(doc.getParagraphs().size());

        assert (newDoc.getParagraphs().size() == doc.getParagraphs().size());
    }

    @Test
    public void testReadingPersistedDoc() throws Exception {
        String targetFile = "src/test/resources/document/json-deserializer-test/model.json";
        String jsonText = Utils.readStringFromFile(targetFile);

        ModelHelper helper = new ModelHelper();
        Document htmlDoc = helper.fromJson(jsonText);

        System.out.println(htmlDoc.getParagraphs().size());
        assert (htmlDoc.getParagraphs().size() == 8);

        List<CoreMap> fragments = htmlDoc.getParagraphs().get(5).get(CoreAnnotations.ParagraphFragmentAnnotation.class);
        assert (fragments
                .get(1)
                .get(CoreAnnotations.IsBoldAnnotation.class) == true);
    }


    @Test
    public void testSerializePhantomHtml() throws Exception {
        String fileName = "src/test/resources/document/json-deserializer-test/test.html";
        String targetFile = "build/resources/test/generated/document/json-deserializer-test/test.html";
        String targetJsonFile = "build/resources/test/generated/document/documentModel.json";

        Files.createParentDirs(new File(targetFile));

        String htmlText = Utils.readStringFromFile(fileName);

        Document htmlDoc = new Document(htmlText);

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.SAVE_HTML_DOCUMENT_TO_FILE, Lists.newArrayList(targetFile))
                        .build();

        Document doc = pipeline.process(htmlDoc);

        // read the file and recreate the doc
        String newJsonText = Utils.readStringFromFile(targetFile);
        Document newDoc = ModelHelper.getModel(newJsonText);

        Utils.writeToFile(targetJsonFile, newJsonText);
        System.out.println(doc.getParagraphs().size());

        assert (newDoc.getParagraphs().size() == doc.getParagraphs().size());
    }


}