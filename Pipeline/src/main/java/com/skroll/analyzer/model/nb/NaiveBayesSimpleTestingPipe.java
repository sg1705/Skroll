package com.skroll.analyzer.model.nb;

import com.google.common.collect.Lists;
import com.skroll.document.Document;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by saurabh on 12/21/14.
 */
public class NaiveBayesSimpleTestingPipe extends SyncPipe<List<String>, Integer> {
    Document output;

    public static final int MAX_TESTING_SENTENCE_LENGTH = BinaryNaiveBayesTrainingPipe.MAX_SENTENCE_LENGTH;

    @Override
    public Integer process(List<String> input) {
        NaiveBayes model = (NaiveBayes)config.get(0);
        // make a unique set of first MAX_LENGTH words
        Set<String> wordSet= new HashSet<String>(
                Lists.partition(input,
                MAX_TESTING_SENTENCE_LENGTH).get(0));

        Pipeline<List<String>, DataTuple> stringsToTrainingTuple =
                new Pipeline.Builder<List<String>, DataTuple>()
                        .add(Pipes.STRINGS_TO_NAIVE_BAYES_DATA_TUPLE, Lists.newArrayList(model, -1))
                        .build();


        Integer output = (model.mostLikelyCategory(stringsToTrainingTuple.process(input)));

        return output;
    }
}
