package com.skroll.analyzer.model.nb;

import com.google.common.collect.Lists;
import com.skroll.document.Document;
import com.skroll.pipeline.SyncPipe;

import java.util.*;

/**
 * Created by saurabh on 12/21/14.
 */
public class BinaryNaiveBayesTestingPipe extends SyncPipe<List<List<String>>, List<Double>> {
    List<Double> output= new ArrayList<Double>();
    public static final int MAX_TESTING_SENTENCE_LENGTH = BinaryNaiveBayesTrainingPipe.MAX_SENTENCE_LENGTH;

    @Override
    public List<Double> process(List<List<String>> input) {
        BinaryNaiveBayesModel model = (BinaryNaiveBayesModel)config.get(0);
        Iterator<List<String>> iterator = input.iterator();

        while(iterator.hasNext()) {
            List<String> eachLine = iterator.next();
            // make a unique set of first MAX_LENGTH words
            Set<String> wordSet= new HashSet<String>(Lists.partition(eachLine,
                    MAX_TESTING_SENTENCE_LENGTH).get(0));
            output.add (model.inferCategoryProbability(wordSet.toArray(new String[wordSet.size()])));
        }
        return output;
    }
}
