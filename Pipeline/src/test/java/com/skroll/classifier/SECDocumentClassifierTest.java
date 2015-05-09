package com.skroll.classifier;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.document.Document;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.ObjectPersistUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.fail;

public class SECDocumentClassifierTest {

    public static void  convertRawToProcessedCorpus (String rawFolder , String ProcessedFolder){
        //use pipeline.parse to parse it

        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(rawFolder));
        for (File f : iterable) {
            if (f.isFile()) {
                String fileName = f.getPath();
                System.out.println(fileName);
                String htmlText = null;

                try {
                    htmlText = Utils.readStringFromFile(fileName);
                    List<String> input = new ArrayList<String>();
                    input.add(htmlText);

                    Pipeline<List<String>, List<String>> paraDefWords =
                            new Pipeline.Builder<List<String>, List<String>>()
                                    .add(Pipes.PARAGRAPH_CHUNKER)
                                    .add(Pipes.PARAGRAPH_REMOVE_BLANK)
                                    .add(Pipes.PARAGRAPH_STOP_WORDS_FILTER)
                                    .add(Pipes.LINE_REMOVE_NBSP_FILTER)
                                    .add(Pipes.REPLACE_SPECIAL_QUOTES_WITH_QUOTES)
                                    .add(Pipes.TRUNCATE_DOCUMENT)
                                            //.add(Pipes.PARAGRAPH_STARTS_WITH_QUOTE_FILTER)
                                    .add(Pipes.LIST_TO_CSV_FILE, Lists.newArrayList(ProcessedFolder + fileName))
                                    .build();

                    paraDefWords.process(input);


                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    }

    public static void  train(Category category, SECDocumentClassifier documentClassifier, String TrainerProcessedDir) throws IOException {

        //list directory first and their name will be category name and lookup their id in global categoy static class.
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(TrainerProcessedDir));
        for (File f : iterable) {
            if (f.isFile()) {
                String fileName = f.getPath();
                System.out.println(fileName);

                try {
                    documentClassifier.train(category, fileName,3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static Map<String, Integer> classify(SECDocumentClassifier documentClassifier, String evaluateDir, int numOfWords) throws IOException {

        //list directory first and their name will be category name and lookup their id in global categoy static class.
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(evaluateDir));
        Map<String, Integer> probableCategory = new HashMap<String, Integer>();

        for (File f : iterable) {
            if (f.isFile()) {
                String fileName = f.getPath();
                System.out.println(fileName);
                String htmlText = null;
                try {
                    htmlText = Utils.readStringFromFile(fileName);
                    Document doc = new Document();
                    doc.setSource(htmlText);
                    //create a pipeline
                    Pipeline<Document, Document> pipeline =
                            new Pipeline.Builder()
                                    .add(Pipes.PARSE_HTML_TO_DOC)
                                    .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                                    .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                                    .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                                    .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                                    .build();

                    doc = pipeline.process(doc);
                    //Document doc = Parser.parseDocumentFromHtml(htmlText);
                    probableCategory.put(fileName, (Integer) documentClassifier.classify(doc, numOfWords));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return probableCategory;
    }

    //@Test
     public void testTrainClassify() throws ObjectPersistUtil.ObjectPersistException {

        SECDocumentClassifier documentClassifier = new SECDocumentClassifier();
        //convertRawToProcessedCorpus(rawFolder, ProcessedFolder);
        try {
            train(new Category(0, "Indentures"), documentClassifier, "src/test/resources/analyzer/train/docclassifier/pdef-words");
            train(new Category(1, "CreditAgreements"), documentClassifier, "src/test/resources/analyzer/train/docclassifier/not-pdef-words");
            documentClassifier.persistModel();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("failed testTrainClassify");
        }

        SECDocumentClassifier documentClassifier1 = new SECDocumentClassifier();
            try {
            System.out.println(classify(documentClassifier1, "src/test/resources/analyzer/evaluate/docclassifier/random-indenture.html", 1000));
        } catch(Exception ex){
            fail("failed testTrainClassify");
        }
    }
}
