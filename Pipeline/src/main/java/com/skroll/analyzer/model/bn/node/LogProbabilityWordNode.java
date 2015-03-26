package com.skroll.analyzer.model.bn.node;

import java.util.Arrays;
import java.util.Map;

/**
 * WordNode stores the random variable representing the distribution of words.
 * When it is used, it actually process all words observations in a data tuple.
 * Created by wei2learn on 3/1/2015.
 */
public class LogProbabilityWordNode extends WordNode{


    public LogProbabilityWordNode(TrainingWordNode trainingNode){
        parameters = trainingNode.getLogProbabilities();
    }
    public LogProbabilityWordNode(LogProbabilityWordNode node){
        parameters = node.getLogProbabilities();
    }

    public double[] sumOutWordsWithObservation(){
        double[] message = new double[parent.getVariable().getFeatureSize()];
        Arrays.fill(message, 0);
        for (String w:observedWords){
            for (int i=0; i<message.length; i++) {
                double[] m = parameters.get(w);
                if (m!=null) message[i] += parameters.get(w)[i];
            }
        }
        return message;
    }

    public Map<String, double[]> getLogProbabilities() {
        return parameters;
    }



}
