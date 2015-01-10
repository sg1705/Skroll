package com.skroll.TrainTest;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.skroll.classification.SECDocumentClassifier;
import com.skroll.classification.category.Category;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by saurabhagarwal on 1/5/15.
 */

public class DocumentTrainEvaluateClient {

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

    public static Map<String, Integer> classify(SECDocumentClassifier documentClassifier, String evaluateDir, int numOfLine) throws IOException {

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
                    Document doc = Parser.parseDocumentFromHtml(htmlText);
                    probableCategory.put(fileName, documentClassifier.classify(fileName, numOfLine));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return probableCategory;
    }

    public static void main(String[] args) throws IOException {

        SECDocumentClassifier documentClassifier = new SECDocumentClassifier();

        //convertRawToProcessedCorpus(rawFolder, ProcessedFolder);

        train(new Category(0, "Indentures"), documentClassifier, "/Users/saurabhagarwal/IdeaProjects/Skroll2015/Pipeline/src/test/resources/analyzer/train/docclassifier/pdef-words");
        train(new Category(1, "CreditAgreements"), documentClassifier, "/Users/saurabhagarwal/IdeaProjects/Skroll2015/Pipeline/src/test/resources/analyzer/train/docclassifier/not-pdef-words");

        System.out.println(classify(documentClassifier, "/Users/saurabhagarwal/IdeaProjects/Skroll2015/Pipeline/src/test/resources/analyzer/evaluate/docclassifier", 3000));


        // All documents in trainer folder, use pipeline.parse to parse it and train the model
        // Main function of DocumentTrainEvaluateClient class will read the trainer folder,
        // parse the file using parser pipeline, store the parsed data into the parsed data directory,
        // feed data to the model and evaluate the document in the evaluator folder.

        //ConvertRawToProcessedCorpus();

        //documentClassification.Train();

        //Map map = documentClassification.Evaluate();

    }


    public static void IterateADirectoryAndEvaluate(String sourceDir) throws IOException {

        File dir = new File(sourceDir);
        File[] subFiles = dir.listFiles();

        if (subFiles != null && subFiles.length > 0) {
            for (File aFile : subFiles) {
                String currentFileName = aFile.getName();
                if (currentFileName.equals(".") || currentFileName.equals("..")) {
                    // skip parent directory and the directory itself
                    continue;
                }
                String filePath = sourceDir + "/" + currentFileName;

                if (aFile.isDirectory()) {
                    // create the directory in saveDir
                    // download the sub directory
                    IterateADirectoryAndEvaluate(filePath);
                } else {
                    if (filePath.endsWith(".htm") && (!filePath.endsWith("-index.htm"))) {
                        // System.out.println("processing the file: " + filePath);
                       // double prob = Tester.testDocclassifier(Parser.parseDocumentFromHtmlFile(filePath), 4000);
                        //if (prob>.90) {
                       // System.out.println(filePath + "\t" + prob );
                        // }
                        // }
                    }
                }
            }
        }
    }




}
