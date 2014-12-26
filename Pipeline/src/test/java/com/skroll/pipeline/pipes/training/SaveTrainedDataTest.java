package com.skroll.pipeline.pipes.training;

import com.skroll.model.HtmlDocument;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

public class SaveTrainedDataTest extends TestCase {

    public void testProcess() throws Exception {

        // read a test file
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        String htmlText = Utils.readStringFromFile(fileName);



        HtmlDocument htmlDoc = new HtmlDocument(htmlText);

        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        //.add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .build();
        htmlDoc = pipeline.process(htmlDoc);

        //create a pipeline
        Pipeline<HtmlDocument, String> saveTrainedPipe =
                new Pipeline.Builder()
                        .add(Pipes.SAVE_TRAINED_DATA)
                        .build();

        String trainedText = saveTrainedPipe.process(htmlDoc);

        //output file name
        String outputFileName = "build/resources/test/SavedTrainedDataTest.txt";
        Utils.writeToFile(outputFileName, trainedText);

    }
}