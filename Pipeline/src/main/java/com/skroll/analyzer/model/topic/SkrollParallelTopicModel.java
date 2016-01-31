package com.skroll.analyzer.model.topic;

import cc.mallet.topics.ParallelTopicModel;
import com.skroll.analyzer.model.bn.inference.BNInference;

/**
 * Created by wei2l on 1/31/2016.
 */
public class SkrollParallelTopicModel extends ParallelTopicModel{
    public SkrollParallelTopicModel(int numberOfTopics) {
        super(numberOfTopics);
    }

    /**
     * return the topic distribution of the word in the corpus.
     * @param type
     * @return
     */
    public double[] getTypeTopicCounts(int type){
        return BNInference.normalize(typeTopicCounts[type],1);
    }
}
