package com.skroll.classifier;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by saurabhagarwal on 8/6/15.
 *  Default Classifier Factory Strategy is responsible for returning the Ids for default classifiers
 */
public class DefaultClassifierFactoryStrategy implements ClassifierFactoryStrategy {

    public List<Integer> getClassifierIds(){
        return Lists.newArrayList(ClassifierFactory.DEF_CLASSIFIER_ID, ClassifierFactory.TOC_CLASSIFIER_ID);
    }

}
