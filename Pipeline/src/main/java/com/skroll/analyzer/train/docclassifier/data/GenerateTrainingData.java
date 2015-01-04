package com.skroll.analyzer.train.docclassifier.data;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by sagupta on 12/12/14.
 */
public class GenerateTrainingData {

    private static final String DOC_ROOT_POSITIVE = "Pipeline/src/main/resources/trainingDocuments/indentures/";
    private static final String DOC_ROOT_NEGATIVE = "Pipeline/src/main/resources/trainingDocuments/creditAgreements/";
    private static final String PARA_DEF_WORDS = "Pipeline/build/resources/generated-files/docclassifier/pdef-words/pdef-words-";
    private static final String NOT_PARA_DEF_WORDS = "Pipeline/build/resources/generated-files/docclassifier/not-pdef-words/not-pdef-words-";


    public static void main(String[] args) throws Exception {

        GenerateTrainingData generateTrainingData = new GenerateTrainingData();
        generateTrainingData.setupDirectories();
        generateTrainingData.generate(DOC_ROOT_POSITIVE, PARA_DEF_WORDS);
        generateTrainingData.generate(DOC_ROOT_NEGATIVE, NOT_PARA_DEF_WORDS);
    }


    public void setupDirectories() throws IOException {
        // create directories first;
        Files.createParentDirs(new File(PARA_DEF_WORDS));
        Files.createParentDirs(new File(NOT_PARA_DEF_WORDS));

    }

    public void generate(String folderName, String destinationFolder) {
        Iterator<File> files = Files.fileTreeTraverser().children(new File(folderName)).iterator();
        while (files.hasNext()) {
            File file = files.next();
            String fileName = folderName+file.getName();

            String htmlText = null;
            try {
                htmlText = Utils.readStringFromFile(fileName);
                List<String> input = new ArrayList<String>();
                input.add(htmlText);

                // defintiion paragraph words only
                Pipeline<List<String>, List<String>> paraDefWords =
                        new Pipeline.Builder<List<String>, List<String>>()
                                .add(Pipes.PARAGRAPH_CHUNKER)
                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                .add(Pipes.PARAGRAPH_STOP_WORDS_FILTER)
                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                .add(Pipes.TRUNCATE_DOCUMENT)
                                //.add(Pipes.PARAGRAPH_STARTS_WITH_QUOTE_FILTER)
                                .add(Pipes.LIST_TO_CSV_FILE, Lists.newArrayList(destinationFolder + file.getName()))
                                .build();

                paraDefWords.process(input);


//                // not definition paragraph words only
//                Pipeline<List<String>, List<String>> notParaDefWords =
//                        new Pipeline.Builder<List<String>, List<String>>()
//                                .add(Pipes.PARAGRAPH_CHUNKER)
//                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
//                                .add(Pipes.PARAGRAPH_STOP_WORDS_FILTER)
//                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
//                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
//                                .add(Pipes.PARAGRAPH_NOT_STARTS_WITH_QUOTE_FILTER)
//                                .add(Pipes.LIST_TO_CSV_FILE,Lists.newArrayList(NOT_PARA_DEF_WORDS + file.getName()))
//                                .build();
//
//                notParaDefWords.process(input);

            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }
}
