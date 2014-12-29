package com.skroll.pipeline;

import com.google.common.io.Files;
import com.skroll.document.HtmlDocument;
import com.skroll.pipeline.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * Created by sagupta on 12/12/14.
 */
public class Tasks {

    private static final String DOC_ROOT = "Pipeline/src/main/resources/trainingDocuments/";
    private static final String DOC_CHUNK = "Pipeline/build/resources/generated-files/trainingData/aa";


    public static void main(String[] args) throws Exception {

        Tasks main = new Tasks();
        main.setupDirectories();
        main.generateTrainingData();
    }


    public void setupDirectories() throws IOException {
        // create directories first;
        Files.createParentDirs(new File(DOC_CHUNK));

    }

    public void generateTrainingData() {
       // Iterator<File> files = Files.fileTreeTraverser().children(new File(DOC_ROOT)).iterator();
        Iterable<File> iterable = Files.fileTreeTraverser().preOrderTraversal(new File(DOC_ROOT));
        Iterator<File> files = iterable.iterator();

        while (files.hasNext()) {
            File file = files.next();
            if (file.isDirectory()) {
                continue;
            }
            String fileName = file.getName();

            String htmlText = null;
            try {
                htmlText = Utils.readStringFromFile(file);
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
                String outputFileName = DOC_CHUNK + fileName;
                Utils.writeToFile(outputFileName, trainedText);


            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }
}
