package com.skroll.analyzer.model;

import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;

/**
 * Helper class to work with models. This class is used to fetch a type of model or even save it back.
 *
 * Created by saurabh on 12/28/14.
 */
public class Models {

    private static BinaryNaiveBayesModel bnbModel = new BinaryNaiveBayesModel();
    private static HiddenMarkovModel hmmModel = new HiddenMarkovModel();

    public static HiddenMarkovModel getHmmModel() {
        return hmmModel;
    }

    public static BinaryNaiveBayesModel getBinaryNaiveBayesModel() {
        return bnbModel;
    }

}
