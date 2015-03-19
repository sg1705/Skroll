package com.skroll.analyzer.model.bn.node;

import java.util.Arrays;
import java.util.Map;

/**
 * WordNode stores the random variable representing the distribution of words.
 * When it is used, it actually process all words observations in a data tuple.
 * Created by wei2learn on 3/1/2015.
 */
public class ProbabilityWordNode extends WordNode{


    Map<String, double[]> probabilityFunction = parameters;

    public ProbabilityWordNode( TrainingWordNode trainingNode){
        probabilityFunction = parameters = trainingNode.getProbabilities();
    }
    public ProbabilityWordNode( ProbabilityWordNode trainingNode){
        probabilityFunction = parameters = trainingNode.getProbabilities();
    }

    public double[] sumOutWordsWithObservation(){
        double[] message = new double[parent.getVariable().getFeatureSize()];
        Arrays.fill(message, 1);
        for (String w:observedWords){
            for (int i=0; i<message.length; i++)
                message[i] *= parameters.get(w)[i];
        }
        return message;
    }

//    double getProbability(String word, int parentValue){
//        return probabilityFunction.get(word) [parentValue];
//    }


    public Map<String, double[]> getProbabilities() {
        return probabilityFunction;
    }



}
