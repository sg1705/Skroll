package com.skroll.analyzer.model.bn.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skroll.analyzer.model.RandomVariableType;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 3/1/2015.
 */
public class TrainingDiscreteNode extends DiscreteNode {
    private static final double PRIOR_COUNT = .1;

    // store the parameters and probability values in a one dimensional array.
    // convert multi-index to the index of the one dimensional array
    // by treating the multi-index as a multi-base representation of integer,
    // least significant index is index 0.
    // todo: in the future, when models are fixed and updated using a lot of data, may consider multiplying by some positive decaying constant less than 1 to reduce the weight of older experiences

    //double[] parameters;
    TrainingDiscreteNode(){}

    public TrainingDiscreteNode(List<RandomVariableType> randomVariables){
        super(randomVariables);
        //parameters = parameters;
        Arrays.fill(parameters, PRIOR_COUNT);
    }

    /**
     * copy constructor
     * @param node
     */
    public TrainingDiscreteNode(TrainingDiscreteNode node){
        this.familyVariables = node.familyVariables;
        parameters= node.getLogProbabilities().clone();
    }


    public void updateCount(){
        updateCount(1);
    }

    public void updateCount(double weight){
        int[] multiIndex= new int[familyVariables.length];
        multiIndex[0] = getObservation();
        for (int i=0; i< parents.length;i++)
            multiIndex[i+1] = parents[i].getObservation();
        updateCount(multiIndex, weight);
    }

    /**
     * currently only used to calculate the prior parameters for word nodes
     * @return
     */
    @JsonIgnore
    double[] getPriorCount(double weight){
        return normalize(weight*parameters.length);
    }

    void updateCount(int[] multiIndex){
        updateCount(multiIndex, 1);
    }

    void updateCount(int[] multiIndex, double weight){
        parameters[ getIndex(multiIndex)] += weight;
    }

    /**
     * convert parameters to probabilities
     */
    @JsonIgnore
    public double[] getProbabilities(){
        //double [] priorCounts = ((TrainingDiscreteNode) parent).getPriorCount();

        double[] probs = new double[parameters.length];
        int numValues = familyVariables[0].getFeatureSize();
        for (int i=0; i< parameters.length; i+=numValues){
            double sum=0;
            for (int j=0; j<numValues; j++) sum += parameters[j+i];
            for (int j=0; j<numValues; j++) probs[i+j] = parameters[i+j]/sum;
        }
        return probs;
    }

    @JsonIgnore
    public double[] getLogProbabilities(){
        //double [] priorCounts = ((TrainingDiscreteNode) parent).getPriorCount();

        double[] probs = new double[parameters.length];
        int numValues = familyVariables[0].getFeatureSize();
        for (int i=0; i< parameters.length; i+=numValues){
            double sum=0;
            for (int j=0; j<numValues; j++) sum += parameters[j+i];
            for (int j=0; j<numValues; j++) probs[i+j] = Math.log(parameters[i+j]/sum);
        }
        return probs;
    }

     @Override
    public String toString() {
        return "TrainingDiscreteNode{" +
                "familyVariables=" + Arrays.toString(familyVariables) +
                ", parameters=" + Arrays.toString(parameters) +

                ", observedValue=" + observedValue +
                '}';
    }
}
