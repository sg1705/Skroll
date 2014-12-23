package com.skroll.pipeline.pipes.document;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.model.HtmlDocument;
import com.skroll.model.ModelHelper;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.util.List;

public class HtmlDocumentToFilePipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        String fileName = "src/test/resources/experiment-jsoup-node-extraction.html";
        String targetFile = "build/resources/test/generated/experiment-jsoup-node-extraction.html";

        Files.createParentDirs(new File(targetFile));

        String htmlText = Utils.readStringFromFile(fileName);

        HtmlDocument htmlDoc = new HtmlDocument((htmlText));

        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.SAVE_HTML_DOCUMENT_TO_FILE, Lists.newArrayList(targetFile))
                        .build();

        HtmlDocument doc = pipeline.process(htmlDoc);

        // read the file and recreate the doc
        String newJsonText = Utils.readStringFromFile(targetFile);
        HtmlDocument newDoc = ModelHelper.getModel(newJsonText);

        assert (newDoc.getParagraphs().size() == doc.getParagraphs().size());
    }

    @Test
    public void testReadingPersistedDoc() throws Exception {
        testProcess();
        String targetFile = "build/resources/test/generated/experiment-jsoup-node-extraction.html";

        String htmlText = Utils.readStringFromFile(targetFile);

        HtmlDocument htmlDoc = ModelHelper.getModel(htmlText);

        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.SAVE_HTML_DOCUMENT_TO_FILE, Lists.newArrayList(targetFile))
                        .build();

        htmlDoc = pipeline.process(htmlDoc);

        // read the file and recreate the doc
        String newJsonText = Utils.readStringFromFile(targetFile);
        HtmlDocument newDoc = ModelHelper.getModel(newJsonText);

        //assert (newDoc.getSourceHtml().equals(htmlDoc.getSourceHtml()));
        assert (newDoc.getParagraphs().size() == htmlDoc.getParagraphs().size());
    }
}