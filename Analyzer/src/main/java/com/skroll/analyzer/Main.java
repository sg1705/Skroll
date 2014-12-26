package com.skroll.analyzer;

import com.google.common.collect.Lists;
import com.skroll.analyzer.nb.BinaryNaiveBayesModel;
import com.skroll.analyzer.nb.BinaryNaiveBayesWithWordsFeatures;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        String[] trainingFolder = {
                "Pipeline/build/resources/generated-files/not-pdef-words",
                "Pipeline/build/resources/generated-files/pdef-words"};

        String testingFolder =
                "Analyzer/src/test/resources/testFolder";

        String testFile =
                "Analyzer/src/test/resources/testData";

        BinaryNaiveBayesModel model = new BinaryNaiveBayesModel();

        Pipeline<String, List<String>> analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_BINARY_NAIVE_BAYES_TRAINER,
                                Lists.newArrayList(model, Constants.CATEGORY_NEGATIVE))
                        .build();

        analyzer.process(trainingFolder[0]);
        analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_BINARY_NAIVE_BAYES_TRAINER,
                                Lists.newArrayList(model, Constants.CATEGORY_POSITIVE))
                        .build();

        analyzer.process(trainingFolder[1]);


        Pipeline<String, String> tester =
                new Pipeline.Builder<String, String>()
                        .add(Pipes.FOLDER_BINARY_NAIVE_BAYES_TESTER,
                                Lists.newArrayList((Object)model))
                        .build();

        String output=tester.process(testingFolder);

        Pipeline<String, String> wordTester =
                new Pipeline.Builder<String, String>()
                        .add(Pipes.FILE_BINARY_NAIVE_BAYES_TESTER_WITH_WORDS_IMPORTANCE,
                                Lists.newArrayList((Object)model))
                        .build();
        output += wordTester.process(testFile);
        //String output=wordTester.process(testFile);
        System.out.println(output);
        System.out.println(model.showWordsImportance());
    }
}
