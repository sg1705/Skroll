package com.skroll.analyzer.train;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import junit.framework.TestCase;

import java.util.List;

public class FolderHTMLHiddenMarkovModelTrainerPipeTest extends TestCase {

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
        //System.out.println(document.toString());

    }
}