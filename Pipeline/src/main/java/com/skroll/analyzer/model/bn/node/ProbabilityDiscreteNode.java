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
     * get updated belief after receiving message from a single parent
     *
     * @param index
     * @param message
     * @return
     */
    double[] getBelief(int index, double[] message){
        double[] belief = new double[parameters.length];
        int sizeUnderIndexFrom = sizeUpTo(index);
        for (int i=0,k=0,messageIndex=0; i<parameters.length; i++,k++){
            if (k == sizeUnderIndexFrom){
                k=0;
                messageIndex ++;
                if (messageIndex == message.length) messageIndex = 0;
            }
            belief[i] = parameters[i] * message[messageIndex];
        }
        return belief;
    }

    /**
     * @param belief
     * @param indexTo the index of the variable to pass message to. The index is for familyVariables
     * @return
     */
     double[] sumOutOtherNodesWithObservationAndBelief(double[] belief, int indexTo ){
        double[] probs = new double[ familyVariables[indexTo].getFeatureSize() ];
        int sizeUnder = sizeUpTo(indexTo);

        for (int i=0,k=0,probsIndex=0; i<belief.length;i++,k++){

            //probs[ (i/sizeUnder) % probs.length] += parameters[i];
            //the following calculation might be slightly more efficient than the line above.
            if (k == sizeUnder){
                k=0;
                probsIndex ++;
                if (probsIndex == probs.length) probsIndex = 0;
            }
            // skip if observed some other value
            if (observedValue >=0 && observedValue != i%familyVariables[0].getFeatureSize()) continue;

            probs[probsIndex] += belief[i];
        }

        return probs;
    }


    // specialized message passing from a single parent to another.
    double[] sumOutOtherNodesWithObservationAndMessage(int indexFrom, double[] message, int indexTo ){
        double[] belief = getBelief(indexFrom, message);
        double[] probs = sumOutOtherNodesWithObservationAndBelief(belief, indexTo);
        return probs;
    }

    public double[] sumOutOtherNodesWithObservationAndMessage(ProbabilityDiscreteNode nodeFrom, double[] message,
                                                              ProbabilityDiscreteNode nodeTo ){
        return sumOutOtherNodesWithObservationAndMessage( getParentNodeIndex(nodeFrom), message, getParentNodeIndex(nodeTo));
    }

    double[] sumOutOtherNodesWithObservation(int indexTo ){
        double[] probs = sumOutOtherNodesWithObservationAndBelief(parameters, indexTo);
        return probs;
    }

    public double[] sumOutOtherNodesWithObservation (ProbabilityDiscreteNode parentNode){
        // +1 to include the current node
        return sumOutOtherNodesWithObservation( getParentNodeIndex(parentNode)+1);
    }

    double getProbability(int index){
        return probabilityFunction[index];
    }

    public double[] getProbabilities(){
        return probabilityFunction;
    }

    @Override
    public String toString() {
        return "BNNode{" +
                "familyVariables=" + Arrays.toString(familyVariables) +
                ", probabilityFunction=" + Arrays.toString(probabilityFunction) +

                ", observedValue=" + observedValue +
                '}';
    }
}
