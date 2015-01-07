package com.skroll.analyzer.model.nb;

import com.google.common.collect.Lists;
import com.skroll.document.Document;
import com.skroll.pipeline.SyncPipe;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by saurabh on 12/21/14.
 */
public class BinaryNaiveBayesSimpleTestingPipe extends SyncPipe<List<String>, Double> {
    Document output;

    public static final int MAX_TESTING_SENTENCE_LENGTH = BinaryNaiveBayesTrainingPipe.MAX_SENTENCE_LENGTH;

    @Override
    public Double process(List<String> input) {
        BinaryNaiveBayesModel model = (BinaryNaiveBayesModel)config.get(0);
        // make a unique set of first MAX_LENGTH words
        Set<String> wordSet= new HashSet<String>(
                Lists.partition(input,
                MAX_TESTING_SENTENCE_LENGTH).get(0));
        Double output = (model.inferCategoryProbabilityMoreStable(wordSet.toArray(new String[wordSet.size()])));

        return output;
    }
}
