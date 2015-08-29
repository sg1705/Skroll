package com.skroll.classifier;

import com.google.common.collect.Lists;
import com.skroll.document.Document;

import java.util.List;

import static com.skroll.classifier.ClassifierId.DOCTYPE_CLASSIFIER;

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
    public List<ClassifierId> getClassifierIds(Document document)   {
        return Lists.newArrayList(DOCTYPE_CLASSIFIER);
    }

}
