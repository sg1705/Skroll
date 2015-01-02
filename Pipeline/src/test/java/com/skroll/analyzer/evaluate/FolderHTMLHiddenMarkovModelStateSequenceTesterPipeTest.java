package com.skroll.analyzer.evaluate;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class FolderHTMLHiddenMarkovModelStateSequenceTesterPipeTest extends TestCase {

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


//        String [] tokens = {"\"", "Notes", "\"","means","the"};
//        String [] tokens = {"\"", "hidden", "\"","markov","model"};
        //String [] tokens = {"\"", "Consolidated", "Net","Tangible", "Assets", "\"","means",",", "with", "respect", "to", "any"};
        String [] tokens = {"\"", "Consolidated", "Net","Tangible", "Assets", "\"","means",",", "with", "respect", "to", "any"};


        System.out.println(Arrays.toString(model.mostLikelyStateSequence(tokens)));
        System.out.println(Arrays.deepToString(model.infer(tokens)));



        String testingFolder =
                "src/test/resources/analyzer/hmmTestingDocs";
        //BinaryNaiveBayesModel model = new BinaryNaiveBayesModel();

        Pipeline<String, String> tester =
                new Pipeline.Builder<String, String>()
                        .add(Pipes.FOLDER_HTML_HIDDEN_MARKOV_MODEL_STATE_SEQUENCE_TESTING_PIPE,
                                Lists.newArrayList((Object)model))
                        .build();

        String output=tester.process(testingFolder);

        //System.out.println(model.showWordsImportance());

        System.out.println(output);

    }
}