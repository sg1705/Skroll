package com.skroll.pipeline.pipes;

import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class AssignParagraphIdsToHTMLDocumentPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        String fileName = "src/test/resources/experiment-jsoup-node-extraction.html";

        String htmlText = Utils.readStringFromFile(fileName);
        List<String> input = new ArrayList<String>();
        input.add(htmlText);

        //create a pipeline
        Pipeline<List<String>, List<String>> pipeline =
                new Pipeline.Builder<List<String>, List<String>>()
                        .add(Pipes.ASSIGN_PARA_IDS_TO_HTML)
                        .build();
        pipeline.process(input);
        }
}