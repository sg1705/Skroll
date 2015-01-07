package com.skroll.analyzer.evaluate;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class FolderNaiveBayesTesterPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        // depends on other tests

        String[] trainingFolder = {
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/not-pdef-words",
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/pdef-words"};
        //BinaryNaiveBayesModel model = new BinaryNaiveBayesModel();
        NaiveBayes model = new NaiveBayes(2, new int[]{2, Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS+1});

        Pipeline<String, List<String>> analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_NAIVE_BAYES_TRAINER,
                                Lists.newArrayList(model, Constants.CATEGORY_NEGATIVE))
                        .build();

        analyzer.process(trainingFolder[0]);
        analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_NAIVE_BAYES_TRAINER,
                                Lists.newArrayList(model, Constants.CATEGORY_POSITIVE))
                        .build();

        analyzer.process(trainingFolder[1]);

        String testingFolder =
                "src/test/resources/analyzer/testFolder";
        //BinaryNaiveBayesModel document = new BinaryNaiveBayesModel();

        Pipeline<String, String> tester =
                new Pipeline.Builder<String, String>()
                        .add(Pipes.FOLDER_NAIVE_BAYES_TESTER,
                                Lists.newArrayList((Object)model))
                        .build();

        String output=tester.process(testingFolder);

        System.out.println(model.showWordsImportance(Constants.CATEGORY_POSITIVE));

        System.out.println(output);
    }

}