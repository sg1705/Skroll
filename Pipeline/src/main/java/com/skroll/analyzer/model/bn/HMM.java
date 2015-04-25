package com.skroll.analyzer.model.bn;

/**
 * HMM is just a sequence of template NBs
 * Created by wei2learn on 3/27/2015.
 */
public class HMM {
    NaiveBayesWithFeatureConditions nbfc;
    double[][] transitionParameters;

    public HMM(){

    }
    public HMM(NaiveBayesWithFeatureConditions nbfc, double[][] transitionParameters){
        this.nbfc = nbfc;
        this.transitionParameters = transitionParameters;
    }
}
