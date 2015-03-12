package com.skroll.analyzer.model.bn.node;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 3/1/2015.
 */
public class ProbabilityDiscreteNode extends DiscreteNode {

    // store the counts and probability values in a one dimensional array.
    // convert multi-index to the index of the one dimensional array
    // by treating the multi-index as a multi-base representation of integer,
    // least significant index is index 0.

    double[] probabilityFunction;

    /**
     * parents, children will be generated outside
     * @param trainingNode
     */
    public ProbabilityDiscreteNode (TrainingDiscreteNode trainingNode){
        this.familyVariables = trainingNode.familyVariables;

        probabilityFunction =parameters= trainingNode.getProbabilities();

    }

    /**
     * copy constructor
     * @param probabilityNode
     */
    public ProbabilityDiscreteNode (ProbabilityDiscreteNode probabilityNode){
        this.familyVariables = probabilityNode.familyVariables;
        probabilityFunction =parameters= probabilityNode.getProbabilities().clone();
    }

    /**
     * return the probabilities after variable at the specified index is summed out
     * Here assuming the variable to be summed out is a parent and it has no parents above
     * @param index the variable index to be summed out
     * @return
     */
    double[] sumOut(int index){
        double[] parentProbs = ((ProbabilityDiscreteNode) (parents[index])).getProbabilities();
        int newSize = probabilityFunction.length / parentProbs.length;
        double[] newProbs = new double[newSize];
        int sizeUnder = sizeUpTo(index);
        int sizeAbove = newSize/sizeUnder;
        for (int i=0; i<sizeUnder; i++){
            for (int j=0; j<newSize; j+=sizeUnder){
                for (int k=0; k<parentProbs.length; k++){
                    newProbs[i+j] += probabilityFunction[i + k*sizeUnder + j*parentProbs.length];
                }
            }
        }

        return newProbs;
    }

    /**
     * return the probabilities after all variables except the variable in the specified indexList are summed out
     * observations are taken into consideration here.
     * @param index the variable index to be left out
     * @return
     */
    public double[] sumOutNodesWithObservationExcept(int index){
        double[] probs = new double[ familyVariables[index].getFeatureSize() ];
        int sizeUnder = sizeUpTo(index);

        for (int i=0; i<parameters.length;i++){
            // skip if observed some other value
            if (observedValue >0 && observedValue != i%familyVariables[0].getFeatureSize()) continue;
            probs[ (i/sizeUnder) % probs.length] += parameters[i];
        }

        return probs;
    }
    public double[] sumOutNodesWithObservationExcept(ProbabilityDiscreteNode parentNode){
        return sumOutNodesWithObservationExcept( getParentNodeIndex(parentNode));
    }


//    public double[] messageTo(int index){
//        double[] message = ((ProbabilityDiscreteNode) (parents[index])).getProbabilities();
//        int newSize = probabilityFunction.length / parentProbs.length;
//        double[] newProbs = new double[newSize];
//        int sizeUnder = sizeUpTo(index);
//        int sizeAbove = newSize/sizeUnder;
//        for (int i=0; i<sizeUnder; i++){
//            for (int j=0; j<newSize; j+=sizeUnder){
//                for (int k=0; k<parentProbs.length; k++){
//                    newProbs[i+j] += probabilityFunction[i + k*sizeUnder + j*parentProbs.length];
//                }
//            }
//        }
//
//        return newProbs;
//    }

    public double getProbability(int index){
        return probabilityFunction[index];
    }

    public double[] getProbabilities(){
        return probabilityFunction;
    }

    /**
     * convert counts to probabilities
     */
//    public void updateProbabilities(){
//        int parentsMultiIndex = 0;
//        int numValues = familyVariables[0].getFeatureSize();
//        for (int i=0; i<counts.length; i+=numValues){
//            double sum=0;
//            for (int j=0; j<numValues; j++) sum += counts[j+i];
//            for (int j=0; j<numValues; j++) probabilityFunction[i+j] = counts[i+j]/sum;
//        }
//    }


    @Override
    public String toString() {
        return "BNNode{" +
                "familyVariables=" + Arrays.toString(familyVariables) +
                ", probabilityFunction=" + Arrays.toString(probabilityFunction) +
                ", parents=" + Arrays.toString(parents) +
                ", children=" + Arrays.toString(children) +
                ", observedValue=" + observedValue +
                '}';
    }
}
