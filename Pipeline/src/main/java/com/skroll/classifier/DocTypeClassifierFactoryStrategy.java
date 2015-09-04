package com.skroll.classifier;

import com.google.common.collect.Lists;
import com.skroll.document.Document;

import java.util.List;

/**
 * Classifier Factory Strategy is responsible for returning the classifier Ids to classify the document.
 *  We can implement this interface to create the strategy for different document types.
 *  Created by saurabhagarwal on 8/6/15.
 */
public class DocTypeClassifierFactoryStrategy implements ClassifierFactoryStrategy {

    /**
     * getCLassifierIds return list of doctype classifier ids.
     * @return
     */
    public List<Integer> getClassifierIds(Document document)   {
        return Lists.newArrayList(ClassifierFactory.DOCTYPE_CLASSIFIER_ID);
    }

}
