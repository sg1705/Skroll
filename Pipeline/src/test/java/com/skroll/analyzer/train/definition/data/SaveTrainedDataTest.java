package com.skroll.analyzer.train.definition.data;

import com.skroll.document.Document;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

public class SaveTrainedDataTest extends TestCase {

    public void testProcess() throws Exception {

        // read a test file
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        String htmlText = Utils.readStringFromFile(fileName);


        Document htmlDoc = new Document(htmlText);

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .build();
        htmlDoc = pipeline.process(htmlDoc);

        //create a pipeline
        Pipeline<Document, String> saveTrainedPipe =
                new Pipeline.Builder()
                        .add(Pipes.SAVE_TRAINED_DATA)
                        .build();

        String trainedText = saveTrainedPipe.process(htmlDoc);

        //output file name
        String outputFileName = "build/resources/test/SavedTrainedDataTest.txt";
        Utils.writeToFile(outputFileName, trainedText);

    }
}