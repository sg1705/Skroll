package com.skroll.analyzer.pipes;

import com.google.common.collect.Lists;
import com.skroll.analyzer.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class FileBinaryNaiveBayesTesterPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {

        String fileName = "src/test/resources/file-nb-trainer-test.txt";
        Object model = new BinaryNaiveBayesModel();

        Pipeline<String, String> analyzer =
                new Pipeline.Builder<String, String>()
                        .add(Pipes.FILE_BINARY_NAIVE_BAYES_TESTER,
                                Lists.newArrayList(model))
                        .build();

        String output = analyzer.process(fileName);
        System.out.println(output);
    }
}