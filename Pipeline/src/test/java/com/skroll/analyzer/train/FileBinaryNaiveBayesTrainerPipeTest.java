package com.skroll.analyzer.train;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FileBinaryNaiveBayesTrainerPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        List<String> fileName = Arrays.asList("src/test/resources/analyzer/file-nb-trainer-test.txt", "src/test/resources/analyzer/file-nb-trainer-test.txt");
        Object model = new BinaryNaiveBayesModel();

        Pipeline<List<String>,List<String>> analyzer =
                new Pipeline.Builder<List<String>, List<String>>()
                        .add(Pipes.FILES_BINARY_NAIVE_BAYES_TRAINER,
                                Lists.newArrayList(model, Constants.CATEGORY_POSITIVE))
                        .build();

        analyzer.process(fileName);
        System.out.println(model.toString());
    }
}