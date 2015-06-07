package com.skroll.analyzer.model.bn;

/**
 * Created by wei on 4/16/15.
 */
public class HMMTrainingHelper {
    public static HMM createTrainingHMM(HMMConfig config){
        return new HMM(
                NBTrainingHelper.createTrainingNBWithFeatureConditioning(config.getNbfcConfig()),
                new double[config.getLength()][config.getLength()]
        );
    }
}
