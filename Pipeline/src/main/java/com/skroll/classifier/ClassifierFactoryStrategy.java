package com.skroll.classifier;

import java.util.List;

/**
 * Created by saurabhagarwal on 8/6/15.
 * Classifier Factory Strategy is responsible for returning the classifier Ids to classify the document.
 *  We can implement this interface to create the strategy for different document types.
 */
public interface ClassifierFactoryStrategy {

    public List<Integer> getClassifierIds();

}
