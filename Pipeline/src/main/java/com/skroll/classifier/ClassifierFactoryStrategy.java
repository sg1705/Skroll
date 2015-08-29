package com.skroll.classifier;

import com.skroll.document.Document;

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
    public  List<ClassifierId> getClassifierIds(Document document);

}
