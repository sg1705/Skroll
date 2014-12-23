package com.skroll.pipeline.pipes.document;

import com.skroll.model.HtmlDocument;
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
        String fileName = "src/test/resources/experiment-jsoup-node-extraction.html";
        String htmlText = Utils.readStringFromFile(fileName);

        HtmlDocument htmlDoc = new HtmlDocument(htmlText);

        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .build();
        HtmlDocument doc = pipeline.process(htmlDoc);
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

//        //compare strings
//        for( int ii = 0; ii < pgs.size(); ii++) {
//            String oldAlgo = pgs.get(ii);
//            String newAlgo = doc.getParagraphs().get(ii).getText();
//            if (!(oldAlgo.equals(newAlgo))) {
//                System.out.println("Old: " + oldAlgo);
//                System.out.println("-----------------");
//                System.out.println("new: " + newAlgo);
//                System.out.println("################");
//            }
//
//        }

        assert (doc.getParagraphs().size() == 4691);

    }
}