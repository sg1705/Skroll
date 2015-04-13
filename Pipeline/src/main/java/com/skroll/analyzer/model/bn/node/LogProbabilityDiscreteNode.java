//package com.skroll.analyzer.model.bn.node;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//import com.skroll.analyzer.model.bn.inference.BNInference;
//
//import java.util.Arrays;
//
///**
// * Created by wei2learn on 3/1/2015.
// */
//public class LogProbabilityDiscreteNode extends DiscreteNode {
//
//    // store the parameters and probability values in a one dimensional array.
//    // convert multi-index to the index of the one dimensional array
//    // by treating the multi-index as a multi-base representation of integer,
//    // least significant index is index 0.
//
//
//    /**
//     * parents, children will be generated outside
//     * @param trainingNode
//     */
//    public LogProbabilityDiscreteNode(TrainingDiscreteNode trainingNode){
//        this.familyVariables = trainingNode.familyVariables;
//
//        parameters= trainingNode.getLogProbabilities();
//
//    }
//
//    /**
//     * copy constructor
//     * @param logProbabilityDiscreteNode
//     */
//    public LogProbabilityDiscreteNode(LogProbabilityDiscreteNode logProbabilityDiscreteNode){
//        this.familyVariables = logProbabilityDiscreteNode.familyVariables;
//        parameters= logProbabilityDiscreteNode.getLogProbabilities().clone();
//    }
//
//    /**
//     * get updated belief after receiving message from a single parent
//     *
//     * @param index
//     * @param message
//     * @return
//     */
//    @JsonIgnore
//    double[] getLogBelief(int index, double[] message){
//        double[] belief = new double[parameters.length];
//        int sizeUnderIndexFrom = sizeUpTo(index);
//        for (int i=0,k=0,messageIndex=0; i<parameters.length; i++,k++){
//            if (k == sizeUnderIndexFrom){
//                k=0;
//                messageIndex ++;
//                if (messageIndex == message.length) messageIndex = 0;
//            }
//            belief[i] = parameters[i] + message[messageIndex];
//        }
//        return belief;
//    }
//
//    /**
//     * This sum out method involves probability additions and does not work in log space.
//     * So the input parameter belief should not be in log space.
//     * @param belief
//     * @param indexTo the index of the variable to pass message to. The index is for familyVariables
//     * @return
//     */
//     double[] sumOutOtherNodesWithObservationAndBelief(double[] belief, int indexTo ){
//        double[] probs = new double[ familyVariables[indexTo].getFeatureSize() ];
//        int sizeUnder = sizeUpTo(indexTo);
//
//        for (int i=0,k=0,probsIndex=0; i<belief.length;i++,k++){
//
//            //probs[ (i/sizeUnder) % probs.length] += parameters[i];
//            //the following calculation might be slightly more efficient than the line above.
//            if (k == sizeUnder){
//                k=0;
//                probsIndex ++;
//                if (probsIndex == probs.length) probsIndex = 0;
//            }
//            // skip if observed some other value
//            if (observedValue >=0 && observedValue != i%familyVariables[0].getFeatureSize()) continue;
//
//            probs[probsIndex] += belief[i];
//        }
//
//        return probs;
//    }
//
//
//    // specialized message passing from a single parent to another.
//    double[] sumOutOtherNodesWithObservationAndMessage(int indexFrom, double[] message, int indexTo ){
//        double[] belief = getLogBelief(indexFrom, message);
//        BNInference.exp(belief);
//        double[] probs = sumOutOtherNodesWithObservationAndBelief(belief, indexTo);
//        BNInference.log(probs); // convert back to log space
//        return probs;
//    }
//
//    public double[] sumOutOtherNodesWithObservationAndMessage(LogProbabilityDiscreteNode nodeFrom, double[] message,
//                                                              LogProbabilityDiscreteNode nodeTo ){
//        return sumOutOtherNodesWithObservationAndMessage( 1+getParentNodeIndex(nodeFrom),
//                message, 1+getParentNodeIndex(nodeTo));
//    }
//
//    double[] sumOutOtherNodesWithObservation(int indexTo ){
//        double[] belief = parameters.clone();
//        BNInference.exp(belief);
//        double[] probs = sumOutOtherNodesWithObservationAndBelief(belief, indexTo);
//        BNInference.log(probs); // convert back to log space
//        return probs;
//    }
//
//    public double[] sumOutOtherNodesWithObservation (LogProbabilityDiscreteNode parentNode){
//        // +1 to include the current node
//        return sumOutOtherNodesWithObservation( getParentNodeIndex(parentNode)+1);
//    }
//
//    @JsonIgnore
//    double getLogProbability(int index){
//        return parameters[index];
//    }
//
//    @JsonIgnore
//    public double[] getLogProbabilities(){
//        return parameters;
//    }
//
//    @Override
//    public String toString() {
//        return "BNNode{" +
//                "familyVariables=" + Arrays.toString(familyVariables) +
//                ", probabilityFunction=" + Arrays.toString(parameters) +
//
//                ", observedValue=" + observedValue +
//                '}';
//    }
//}
