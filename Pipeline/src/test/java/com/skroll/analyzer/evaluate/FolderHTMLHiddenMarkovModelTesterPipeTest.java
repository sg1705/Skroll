package com.skroll.analyzer.evaluate;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import junit.framework.TestCase;

import java.util.List;

public class FolderHTMLHiddenMarkovModelTesterPipeTest extends TestCase {

    public void testProcess() throws Exception {

        String trainingFolder = "src/test/resources/analyzer/hmmTrainingDocs";
        HiddenMarkovModel model = new HiddenMarkovModel(12);

        Pipeline<String, List<String>> analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE,
                                Lists.newArrayList((Object)model))
                        .build();

        analyzer.process(trainingFolder);
        model.updateProbabilities();;
        System.out.println(model.showProbabilities());
        System.out.println(model.showCounts());





        String testingFolder =
                "src/test/resources/analyzer/hmmTestingDocs";
        //BinaryNaiveBayesModel model = new BinaryNaiveBayesModel();

        Pipeline<String, String> tester =
                new Pipeline.Builder<String, String>()
                        .add(Pipes.FOLDER_HTML_HIDDEN_MARKOV_MODEL_TESTING_PIPE,
                                Lists.newArrayList((Object)model))
                        .build();

        String output=tester.process(testingFolder);

        //System.out.println(model.showWordsImportance());

        System.out.println(output);

    }
}