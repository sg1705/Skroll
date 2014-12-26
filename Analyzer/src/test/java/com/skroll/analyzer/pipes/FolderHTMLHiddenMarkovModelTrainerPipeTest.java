package com.skroll.analyzer.pipes;

import com.google.common.collect.Lists;
import com.skroll.analyzer.hmm.HiddenMarkovModel;
import com.skroll.analyzer.nb.BinaryNaiveBayesModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import junit.framework.TestCase;

import java.io.File;
import java.util.List;

public class FolderHTMLHiddenMarkovModelTrainerPipeTest extends TestCase {

    public void testProcess() throws Exception {

        String trainingFolder = "src/test/resources/hmmTrainingDocs";
        HiddenMarkovModel model = new HiddenMarkovModel();

        Pipeline<String, List<String>> analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE,
                                Lists.newArrayList((Object)model))
                        .build();

        analyzer.process(trainingFolder);
        model.updateProbabilities();;
        System.out.println(model.showProbabilities());
        System.out.println(model.showCounts());
        //System.out.println(model.toString());

    }
}