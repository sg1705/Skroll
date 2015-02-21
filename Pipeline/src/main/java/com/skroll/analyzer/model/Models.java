package com.skroll.analyzer.model;

import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.pipeline.util.Constants;

/**
 * Helper class to work with models. This class is used to fetch a type of document or even save it back.
 *
 * Created by saurabh on 12/28/14.
 */
public class Models {

    private static NaiveBayes nbModelForDefinitions = new NaiveBayes(2, Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_FEATURE_SIZES);
    private static HiddenMarkovModel hmmModel = new HiddenMarkovModel();

    public static HiddenMarkovModel getHmmModel() {
        return hmmModel;
    }

    public static NaiveBayes getNaiveBayesForDefinitionsModel() {return nbModelForDefinitions;}

}
