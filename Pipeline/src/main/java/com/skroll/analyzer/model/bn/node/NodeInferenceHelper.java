package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.inference.BNInference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 4/11/15.
 */
public class NodeInferenceHelper {
    public static DiscreteNode createLogProbabilityDiscreteNode(DiscreteNode trainingNode, List<DiscreteNode> parents) {
        DiscreteNode node = new DiscreteNode(parents.toArray(new DiscreteNode[parents.size()]));
        node.setFamilyVariables( trainingNode.getFamilyVariables());
        node.setParameters(NodeTrainingHelper.getLogProbabilities(trainingNode));
        return node;
    }

    public static DiscreteNode createLogProbabilityDiscreteNode(DiscreteNode trainingNode) {
        return createLogProbabilityDiscreteNode(trainingNode, new ArrayList<DiscreteNode>());
    }


    /**
     * get updated belief after receiving message from a single parent
     *
     * @param index
     * @param message
     * @return
     */
    static double[] getLogBelief(DiscreteNode node, int index, double[] message){
        double[] belief = node.copyOfParameters();
        int sizeUnderIndexFrom = node.sizeUpTo(index);
        for (int i=0,k=0,messageIndex=0; i<belief.length; i++,k++){
            if (k == sizeUnderIndexFrom){
                k=0;
                messageIndex ++;
                if (messageIndex == message.length) messageIndex = 0;
            }
            belief[i] = belief[i] + message[messageIndex];
        }
        return belief;
    }

    /**
     * This sum out method involves probability additions and does not work in log space.
     * So the input parameter belief should not be in log space.
     * @param belief
     * @param indexTo the index of the variable to pass message to. The index is for familyVariables
     * @return
     */
    static double[] sumOutOtherNodesWithObservationAndBelief(DiscreteNode node, double[] belief, int indexTo ){
        RandomVariable[] familyVariables = node.getFamilyVariables();
        double[] probs = new double[ familyVariables[indexTo].getFeatureSize() ];
        int sizeUnder = node.sizeUpTo(indexTo);

        for (int i=0,k=0,probsIndex=0; i<belief.length;i++,k++){

            //probs[ (i/sizeUnder) % probs.length] += parameters[i];
            //the following calculation might be slightly more efficient than the line above.
            if (k == sizeUnder){
                k=0;
                probsIndex ++;
                if (probsIndex == probs.length) probsIndex = 0;
            }
            // skip if observed some other value
            int observedValue = node.getObservation();
            if (observedValue >=0 && observedValue != i%familyVariables[0].getFeatureSize()) continue;

            probs[probsIndex] += belief[i];
        }

        return probs;
    }

    // specialized message passing from a single parent to another.
    static double[] sumOutOtherNodesWithObservationAndMessage(DiscreteNode  node, int indexFrom, double[] message, int indexTo ){
        double[] belief = getLogBelief(node, indexFrom, message);
        BNInference.exp(belief);
        double[] probs = sumOutOtherNodesWithObservationAndBelief(node, belief, indexTo);
        BNInference.log(probs); // convert back to log space
        return probs;
    }


    public static double[] sumOutOtherNodesWithObservationAndMessage(DiscreteNode node, DiscreteNode nodeFrom,
                                                              double[] message, DiscreteNode nodeTo ){
        return sumOutOtherNodesWithObservationAndMessage(node, 1+node.getParentNodeIndex(nodeFrom),
                message, 1+node.getParentNodeIndex(nodeTo));
    }

    static double[] sumOutOtherNodesWithObservation(DiscreteNode node, int indexTo ){
        double[] belief = node.copyOfParameters();
        BNInference.exp(belief);
        double[] probs = sumOutOtherNodesWithObservationAndBelief(node, belief, indexTo);
        BNInference.log(probs); // convert back to log space
        return probs;
    }

    public static double[] sumOutOtherNodesWithObservation (DiscreteNode node, DiscreteNode parentNode){
        // +1 to include the current node
        return sumOutOtherNodesWithObservation(node, node.getParentNodeIndex(parentNode)+1);
    }




    public static WordNode createLogProbabilityWordNode(WordNode trainingNode, DiscreteNode parentNode){
        WordNode node = new WordNode(parentNode);
        node.setParameters( NodeTrainingHelper.getLogProbabilities(trainingNode));
        return node;
    }

    public static double[] sumOutWordsWithObservation(WordNode node){

        double[] message = new double[node.getParent().getVariable().getFeatureSize()];
        Arrays.fill(message, 0);
        for (String w: node.getObservation()){
            for (int i=0; i<message.length; i++) {
                double[] m = node.getParameters().get(w);
                if (m!=null) message[i] += node.getParameters().get(w)[i];
            }
        }
        return message;
    }

}