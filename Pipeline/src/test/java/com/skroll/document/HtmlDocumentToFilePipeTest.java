package com.skroll.document;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;

public class HtmlDocumentToFilePipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        String fileName = "src/test/resources/document/experiment-jsoup-node-extraction.html";
        String targetFile = "build/resources/test/generated/document/experiment-jsoup-node-extraction.html";

        Files.createParentDirs(new File(targetFile));

        String htmlText = Utils.readStringFromFile(fileName);

        Document htmlDoc = new Document((htmlText));

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

        assert (newDoc.getParagraphs().size() == doc.getParagraphs().size());
    }

    @Test
    public void testReadingPersistedDoc() throws Exception {
        testProcess();
        String targetFile = "build/resources/test/generated/document/experiment-jsoup-node-extraction.html";

        String htmlText = Utils.readStringFromFile(targetFile);

        Document htmlDoc = ModelHelper.getModel(htmlText);

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.SAVE_HTML_DOCUMENT_TO_FILE, Lists.newArrayList(targetFile))
                        .build();

        htmlDoc = pipeline.process(htmlDoc);

        // read the file and recreate the doc
        String newJsonText = Utils.readStringFromFile(targetFile);
        Document newDoc = ModelHelper.getModel(newJsonText);

        //assert (newDoc.getSourceHtml().equals(htmlDoc.getSourceHtml()));
        assert (newDoc.getParagraphs().size() == htmlDoc.getParagraphs().size());
    }
}