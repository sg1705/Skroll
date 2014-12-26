package com.skroll.analyzer.pipes;

import com.google.common.collect.Lists;
import com.skroll.analyzer.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class FolderBinaryNaiveBayesTesterPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        // depends on other tests

        String[] trainingFolder = {
                "../Pipeline/build/resources/generated-files/not-pdef-words",
                "../Pipeline/build/resources/generated-files/pdef-words"};
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

        String testingFolder =
                "src/test/resources/testFolder";
        //BinaryNaiveBayesModel model = new BinaryNaiveBayesModel();

        Pipeline<String, String> tester =
                new Pipeline.Builder<String, String>()
                        .add(Pipes.FOLDER_BINARY_NAIVE_BAYES_TESTER,
                                Lists.newArrayList((Object)model))
                        .build();

        String output=tester.process(testingFolder);

        //System.out.println(model.showWordsImportance());

        System.out.println(output);
    }

}