package com.skroll.analyzer.train.definition.data;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
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

    private static final String DOC_ROOT = "Pipeline/src/main/resources/";
    private static final String DOC_CHUNK = "Pipeline/build/resources/generated-files/doc/doc-";
    private static final String DOC_WORDS = "Pipeline/build/resources/generated-files/doc-words/words-";
    private static final String PARA_WORDS = "Pipeline/build/resources/generated-files/para-words/pwords-";
    private static final String PARA_DEF = "Pipeline/build/resources/generated-files/pdef/pdef-";
    private static final String PARA_DEF_WORDS = "Pipeline/build/resources/generated-files/pdef-words/pdef-words-";
    private static final String NOT_PARA_DEF = "Pipeline/build/resources/generated-files/not-pdef/not-pdef-";
    private static final String NOT_PARA_DEF_WORDS = "Pipeline/build/resources/generated-files/not-pdef-words/not-pdef-words-";
    private static final String PARA_DEF_TERMS = "Pipeline/build/resources/generated-files/pdef-terms/pdef-terms-";


    public static void main(String[] args) throws Exception {

        GenerateTrainingData generateTrainingData = new GenerateTrainingData();
        generateTrainingData.setupDirectories();
        generateTrainingData.generateTrainingData();
    }


    public void setupDirectories() throws IOException {
        // create directories first;
        Files.createParentDirs(new File(DOC_CHUNK));
        Files.createParentDirs(new File(DOC_WORDS));
        Files.createParentDirs(new File(PARA_WORDS));
        Files.createParentDirs(new File(PARA_DEF));
        Files.createParentDirs(new File(PARA_DEF_WORDS));
        Files.createParentDirs(new File(NOT_PARA_DEF));
        Files.createParentDirs(new File(NOT_PARA_DEF_WORDS));
        Files.createParentDirs(new File(PARA_DEF_TERMS));

    }

    public void generateTrainingData() {
        Iterator<File> files = Files.fileTreeTraverser().children(new File("Pipeline/src/main/resources")).iterator();
        while (files.hasNext()) {
            File file = files.next();
            String fileName = DOC_ROOT+file.getName();

            String htmlText = null;
            try {
                htmlText = Utils.readStringFromFile(fileName);
                List<String> input = new ArrayList<String>();
                input.add(htmlText);

                //create a pipeline
                Pipeline<List<String>, List<String>> documentChunkingPipeline =
                        new Pipeline.Builder<List<String>, List<String>>()
                                .add(Pipes.PARAGRAPH_CHUNKER)
                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                .add(Pipes.LIST_TO_CSV_FILE, Lists.newArrayList(DOC_CHUNK + file.getName()))
                                .build();
                List<String> paragraphs = documentChunkingPipeline.process(input);

                Pipeline<List<String>, List<String>> paraWords =
                        new Pipeline.Builder<List<String>, List<String>>()
                                .add(Pipes.PARAGRAPH_CHUNKER)
                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                .add(Pipes.PARAGRAPH_STOP_WORDS_FILTER)
                                .add(Pipes.LIST_TO_CSV_FILE,Lists.newArrayList(PARA_WORDS+file.getName()))
                                .build();

                List<String> output = paraWords.process(input);


                Pipeline<List<String>, List<String>> docWords =
                        new Pipeline.Builder<List<String>, List<String>>()
                                .add(Pipes.PARAGRAPH_CHUNKER)
                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                .add(Pipes.DOCUMENT_TOKENIZE_WORD)
                                .add(Pipes.DOCUMENT_COUNT_WORD)
                                .add(Pipes.LIST_TO_CSV_FILE,Lists.newArrayList(DOC_WORDS+file.getName()))
                                .build();

                List<String> aggregateWordsInDoc = docWords.process(input);

                //defintion paragraph only
                Pipeline<List<String>, List<String>> paraDef =
                        new Pipeline.Builder<List<String>, List<String>>()
                                .add(Pipes.PARAGRAPH_CHUNKER)
                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                .add(Pipes.PARAGRAPH_STARTS_WITH_QUOTE_FILTER)
                                .add(Pipes.LIST_TO_CSV_FILE, Lists.newArrayList(PARA_DEF + file.getName()))
                                .build();
                paraDef.process(input);

                // defintiion paragraph words only
                Pipeline<List<String>, List<String>> paraDefWords =
                        new Pipeline.Builder<List<String>, List<String>>()
                                .add(Pipes.PARAGRAPH_CHUNKER)
                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                .add(Pipes.PARAGRAPH_STOP_WORDS_FILTER)
                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                .add(Pipes.PARAGRAPH_STARTS_WITH_QUOTE_FILTER)
                                .add(Pipes.LIST_TO_CSV_FILE,Lists.newArrayList(PARA_DEF_WORDS+file.getName()))
                                .build();

                paraDefWords.process(input);

                //defintion paragraph only
                Pipeline<List<String>, List<String>> notParaDef =
                        new Pipeline.Builder<List<String>, List<String>>()
                                .add(Pipes.PARAGRAPH_CHUNKER)
                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                .add(Pipes.PARAGRAPH_NOT_STARTS_WITH_QUOTE_FILTER)
                                .add(Pipes.LIST_TO_CSV_FILE, Lists.newArrayList(NOT_PARA_DEF + file.getName()))
                                .build();
                notParaDef.process(input);

                // defintiion paragraph words only
                Pipeline<List<String>, List<String>> notParaDefWords =
                        new Pipeline.Builder<List<String>, List<String>>()
                                .add(Pipes.PARAGRAPH_CHUNKER)
                                .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                .add(Pipes.PARAGRAPH_STOP_WORDS_FILTER)
                                .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                .add(Pipes.PARAGRAPH_NOT_STARTS_WITH_QUOTE_FILTER)
                                .add(Pipes.LIST_TO_CSV_FILE,Lists.newArrayList(NOT_PARA_DEF_WORDS + file.getName()))
                                .build();

                notParaDefWords.process(input);

                Document htmlDoc = new Document(htmlText);

                //create a pipeline
                Pipeline<Document, Document> pipeline =
                        new Pipeline.Builder()
                                .add(Pipes.PARSE_HTML_TO_DOC)
                                .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                                .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                                .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                                .add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                                .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                                .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                                .build();
                htmlDoc = pipeline.process(htmlDoc);
                List<String> defList = new ArrayList<String>();
                int count = 0;
                for(CoreMap paragraph : htmlDoc.getParagraphs()) {
                    String words = Joiner.on(",").join(DocumentHelper
                            .getTokenString(
                                    paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class)));
                    defList.add(words);
                    System.out.println(words);
                }

                // defintiion only
                Pipeline<List<String>, List<String>> pDefTerms =
                        new Pipeline.Builder()
                                .add(Pipes.LIST_TO_CSV_FILE, Lists.newArrayList(PARA_DEF_TERMS + file.getName()))
                                .build();

                pDefTerms.process(defList);



            } catch (Exception e) {
                e.printStackTrace();
            }


        }

    }
}
