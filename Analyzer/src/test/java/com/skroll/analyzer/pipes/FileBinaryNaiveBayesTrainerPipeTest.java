package com.skroll.analyzer.pipes;

import com.google.common.collect.Lists;
import com.skroll.analyzer.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileBinaryNaiveBayesTrainerPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        List<String> fileName = Arrays.asList("src/test/resources/file-nb-trainer-test.txt", "src/test/resources/file-nb-trainer-test.txt");
        Object model = new BinaryNaiveBayesModel();

        Pipeline<List<String>,List<String>> analyzer =
                new Pipeline.Builder<List<String>, List<String>>()
                        .add(Pipes.FILES_BINARY_NAIVE_BAYES_TRAINER,
                                Lists.newArrayList(model, BinaryNaiveBayesModel.CATEGORY_POSITIVE))
                        .build();

        analyzer.process(fileName);
        System.out.println(model.toString());
    }
}