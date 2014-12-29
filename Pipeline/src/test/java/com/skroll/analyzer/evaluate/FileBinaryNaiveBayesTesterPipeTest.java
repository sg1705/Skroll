package com.skroll.analyzer.evaluate;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import junit.framework.TestCase;
import org.junit.Test;

public class FileBinaryNaiveBayesTesterPipeTest extends TestCase {

    @Test
    public void testProcess() {

        String fileName = "src/test/resources/analyzer/file-nb-trainer-test.txt";
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