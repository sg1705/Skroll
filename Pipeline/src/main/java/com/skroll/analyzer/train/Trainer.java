package com.skroll.analyzer.train;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.Models;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;

import java.io.File;
import java.util.List;

/**
 *
 * Class used for training a document.
 *
 * Created by saurabh on 12/28/14.
 */
public class Trainer {

    public static void trainBinaryNaiveBayes(String fileName, int category) {
        //check to see if this is a directory
        File file = new File(fileName);
        if (file.isDirectory()) {
            Pipeline pipeline = new Pipeline.Builder<String, List<String>>()
                    .add(Pipes.FOLDER_BINARY_NAIVE_BAYES_TRAINER,
                            Lists.newArrayList(
                                    Models.getBinaryNaiveBayesModel(),
                                    category))
                    .build();

            pipeline.process(fileName);
        } else {
            Pipeline pipeline = new Pipeline.Builder<String, List<String>>()
                    .add(Pipes.FILE_BINARY_NAIVE_BAYES_TRAINER,
                            Lists.newArrayList(
                                    Models.getBinaryNaiveBayesModel(),
                                    category))
                    .build();

            pipeline.process(fileName);
        }
    }

    public static void trainHiddenMarkovModel(String fileName) {
        Pipeline<String, List<String>> analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE,
                                Lists.newArrayList((Object) Models.getHmmModel()))
                        .build();

        analyzer.process(fileName);
        Models.getHmmModel().updateProbabilities();
    }

}
