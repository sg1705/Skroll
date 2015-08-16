package com.skroll.classifier;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Classifier Factory Strategy is responsible for returning the classifier Ids to classify the document.
 *  We can implement this interface to create the strategy for different document types.
 *  Created by saurabhagarwal on 8/6/15.
 */
public interface ClassifierFactoryStrategy {

    /**
     * Default implementation of getCLassifierIds return list of classifier ids.
     * @return
     */
    public default List<Integer> getClassifierIds()   {
        return Lists.newArrayList(ClassifierFactory.DEF_CLASSIFIER_ID, ClassifierFactory.TOC_CLASSIFIER_ID);
    }

}
