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
                       List<RandomVariableType> documentFeatureVarList, List<RandomVariableType> wordVarList ){

        nb = new TrainingNaiveBayesWithFeatureConditions(stateType, featureVarList,
                featureExistsAtDocLevelVarList, documentFeatureVarList, wordVarList );
    }

    public void addSample(SimpleDataTuple[] tuples, double weight){
        for (int i=0; i<tuples.length; i++){
            if (i+1<tuples.length){
                int state = tuples[i].getDiscreteValues()[0];
                int nextState = tuples[i+1].getDiscreteValues()[0];
                transitionParameters[state][nextState] += weight;
            }
            ((TrainingNaiveBayesWithFeatureConditions) nb).addSample(tuples[i]);
        }
    }
}
