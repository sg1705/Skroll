package com.skroll.analyzer.nb;

import com.google.common.collect.Lists;
import com.skroll.pipeline.SyncPipe;

import java.util.*;

/**
 * Created by saurabh on 12/21/14.
 */
public class BinaryNaiveBayesTrainingPipe extends SyncPipe<List<List<String>>, List<List<String>>> {


    public static final int MAX_SENTENCE_LENGTH = 8;

    @Override
    public List<List<String>> process(List<List<String>> input) {
        BinaryNaiveBayesModel model = (BinaryNaiveBayesModel)config.get(0);
        int categoryType = (Integer)config.get(1);

        Iterator<List<String>> iterator = input.iterator();

        while(iterator.hasNext()) {
            List<String> eachLine = iterator.next();
            if (eachLine.size() < MAX_SENTENCE_LENGTH) {
                continue;
            }


            // make a unique set of first MAX_LENGTH words
            Set<String> wordSet= new HashSet<String>(Lists.partition(eachLine, MAX_SENTENCE_LENGTH).get(0));

            if (wordSet.contains("\"")){
                if (eachLine.size()<=MAX_SENTENCE_LENGTH) continue;
                wordSet.add(eachLine.get(MAX_SENTENCE_LENGTH));
            }

            // increase the category count in the model
            model.incrementCategory(categoryType);

            Iterator<String> wordIterator = wordSet.iterator();
            while (wordIterator.hasNext()) {
                String word = wordIterator.next();
                //TODO collapse this into other pipes
                word = word.replace("\u201c","\"");
                model.addWord(categoryType, word);
            }

        }
        return input;
    }


}
