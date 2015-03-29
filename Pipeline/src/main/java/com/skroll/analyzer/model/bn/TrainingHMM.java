package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariableType;

import java.util.List;

/**
 * Created by wei2learn on 3/27/2015.
 */
public class TrainingHMM extends HMM {
    public TrainingHMM(int modelLength, RandomVariableType stateType,
                       List<RandomVariableType> featureVarList,
                       List<RandomVariableType> featureExistsAtDocLevelVarList,
                       List<RandomVariableType> documentFeatureVarList){

        nb = new TrainingNaiveBayesWithFeatureConditions(stateType, featureVarList,
                featureExistsAtDocLevelVarList, documentFeatureVarList);
    }
    public void addSample(SimpleDataTuple[] tuples){

    }
}
