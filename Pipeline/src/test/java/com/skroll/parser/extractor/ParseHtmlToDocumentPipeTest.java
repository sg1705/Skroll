package com.skroll.parser.extractor;

import com.skroll.document.Document;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ParseHtmlToDocumentPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        String fileName = "src/test/resources/parser/extractor/experiment-jsoup-node-extraction.html";
        String htmlText = Utils.readStringFromFile(fileName);

        Document htmlDoc = new Document(htmlText);

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .build();
        Document doc = pipeline.process(htmlDoc);
        System.out.println(doc.getParagraphs().size());

        List<String> input = new ArrayList<String>();
        input.add(htmlText);
        //create a pipeline
        Pipeline<List<String>, List<String>> documentChunkingPipeline =
                new Pipeline.Builder<List<String>, List<String>>()
                        .add(Pipes.PARAGRAPH_CHUNKER)
                        .build();

        List<String> pgs = documentChunkingPipeline.process(input);
        System.out.println(pgs.size());

    }
}