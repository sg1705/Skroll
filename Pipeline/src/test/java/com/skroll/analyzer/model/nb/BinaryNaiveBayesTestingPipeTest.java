package com.skroll.analyzer.model.nb;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class BinaryNaiveBayesTestingPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {

        String fileName = "src/test/resources/analyzer/file-nb-trainer-test.txt";


        Pipeline<String, List<String>> fileIntoString =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FILE_INTO_LIST_OF_STRING)
                        .build();


        Pipeline<List<String>, List<List<String>>> csvSplitPipeline =
                new Pipeline.Builder<List<String>, List<List<String>>>()
                        .add(Pipes.CSV_SPLIT_INTO_LIST_OF_STRING)
                        .build();



        List<String> fileStrings = fileIntoString.process(fileName);
        List<List<String>> csvStrings = csvSplitPipeline.process(fileStrings);

        Object model = new BinaryNaiveBayesModel();


        Pipeline<List<List<String>>,List<List<String>>> trainer =
                new Pipeline.Builder<List<List<String>>, List<List<String>>>()
                        .add(Pipes.BINARY_NAIVE_BAYES_TRAINING,
                                Lists.newArrayList(model, Constants.CATEGORY_POSITIVE))
                        .build();

        trainer.process(csvStrings);
        Pipeline<List<List<String>>,List<Double>> tester =
                new Pipeline.Builder<List<List<String>>, List<Double>>()
                        .add(Pipes.BINARY_NAIVE_BAYES_TESTING,
                                Lists.newArrayList(model))
                        .build();

        List<Double> output=tester.process(csvStrings);
        System.out.println(output);
    }

}