package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.inference.BNInference;

import javax.xml.soap.Node;
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
        int sizeUnderIndexFrom = NodeHelper.sizeUpTo(node, index);
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
        int sizeUnder = NodeHelper.sizeUpTo(node, indexTo);

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

            //require the node to be observed. Otherwise, all probabilities will sum up to 1.
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
        return sumOutOtherNodesWithObservation(node, node.getParentNodeIndex(parentNode) + 1);
    }

    public static MultiplexNode createLogProbabilityMultiplexNode(MultiplexNode trainingNode,
                                                                  DiscreteNode selectingNode,
                                                                  List<DiscreteNode> parents) {
        MultiplexNode multiNode = new MultiplexNode(selectingNode);
        DiscreteNode[] tNodes = trainingNode.getNodes();
        DiscreteNode[] nodes = new DiscreteNode[tNodes.length];
//        nodes[0] = NodeInferenceHelper.createLogProbabilityDiscreteNode(tNodes[0]);
        for (int n = 0; n < nodes.length; n++) {
            nodes[n] = NodeInferenceHelper.createLogProbabilityDiscreteNode(tNodes[n], Arrays.asList(parents.get(n)));
        }
        multiNode.setNodes(nodes);
        return multiNode;
    }

    /**
     * Message passing for multiplex node.
     * Pass messages to selectingNode from all other parents.
     * Requiring the node observed.
     *
     * @param multiNode
     * @param messages
     * @return
     */
    public static double[] updateMessageToSelectingNode(MultiplexNode multiNode, double[][] messages) {
        DiscreteNode[] nodes = multiNode.getNodes();
        int observedValue = multiNode.getObservation();
        double[] newMessage = new double[nodes.length];
        // todo: for the none node in the multinode, should we pass message here for consistency, or just do it in the initial belief update?
        for (int n = 0; n < nodes.length; n++) {
            double[] parameters = nodes[n].getParameters();
            newMessage[n] = Math.exp(parameters[observedValue] + messages[n][0]);
            newMessage[n] += Math.exp(parameters[observedValue + nodes[n].getVariable().getFeatureSize()] + messages[n][1]);
            newMessage[n] = Math.log(newMessage[n]);
//            newMessage[n-1] = sumOutOtherNodesWithObservationAndMessage(nodes[n], nodes[n].getParents()[0],
//                    messages[n-1], multiNode.getSelectingNode())[0];
        }

        return newMessage;

    }

    /**
     * this method trys to compute p(dik)*sum_j{ p(f|cj,dik) * p(cj) } = p(dik,f) ~ p(dik|f) for each di,
     * where ~ means proportional to
     * f is the observed paragraph feature, dik is the ith doc feature, k represent true or false,
     * cj is the jth paragraph category.
     * Now if i==j, then p(f|cj,djk) can be directly read from the parameters
     * if i!=j, then we use an approximation p(f|cj,dik) ~= p(f|cj, dj0), where dj0 represent doc feature j is false.
     * The idea behind this approximation is that if the doc feature doesn't match the category we're looking at, then
     * the doc feature condition is treated disabled, and so we approximate it with no doc feature conditions,
     * which is approximated the doc feature condition set to false.
     * <p>
     * So now with this approximation,
     * p(dik)*sum_j{ p(f|cj,dik) * p(cj) }  ~= p(dik)*(p(f|ci,dik) * p(ci) + sum_{j!=i}p(f|cj,dj0) * p(cj) )
     * <p>
     * for efficiency, calculate  sum_{j}p(f|cj,dj0) * p(cj) and then determine sum_{j!=i}p(f|cj,dj0) * p(cj)
     * todo: check to see how good is this aproaximation.
     *
     * @param multiNode
     * @param messages
     * @return
     */
    public static double[][] updateMessagesFromSelectingNode(MultiplexNode multiNode, double[] messages) {
        DiscreteNode[] nodes = multiNode.getNodes();
        double[][] newMessages = new double[nodes.length][2]; // the none node does not pass message
        int observedValue = multiNode.getObservation();

        int featureSize = nodes[0].getVariable().getFeatureSize();
        double sum = 0; // the probability of the observed feature value. This involves summing the probabilities, so not in log space
        // calculates sum_cd{ p(c) * p(f|c,d) } = sum_cd{ p(fcd)/p(d) } ~
        for (int n = 0; n < nodes.length; n++) {
            sum += Math.exp(messages[n] + nodes[n].getParameter(observedValue));
//            pF += Math.exp(messages[n] + nodes[n].getParameter(observedValue + featureSize));
        }

        for (int n = 0; n < nodes.length; n++) {

////            double pFC = Math.exp(messages[n] + nodes[n].getParameter(observedValue)) +
////                    Math.exp(messages[n] + nodes[n].getParameter(observedValue + featureSize));
//            double pFNotC = pF - pFC; // here pFNotC is used to approximate p(F|notC,D) and p(F|notC,notD)
            double pFNotC = sum - Math.exp(messages[n] + nodes[n].getParameter(observedValue));

            // make use of the fact the parent var is binary.
//            newMessages[n][0] = nodes[n].getParameters()[observedValue];
            newMessages[n][1] = nodes[n].getParameters()[observedValue + nodes[n].getVariable().getFeatureSize()];

//            newMessages[n][0] += messages[n];
            newMessages[n][1] += messages[n];

//            newMessages[n][0] = Math.log(Math.exp(messages[n]) + pFNotC);
            newMessages[n][0] = Math.log(sum);
            newMessages[n][1] = Math.log(Math.exp(newMessages[n][1]) + pFNotC);
        }
        return newMessages;
    } // todo: the current implementation would make all newMessage[n][0] the same? does it make sense?
    //   is this going to make all the corresponding doc beliefs the same?
    //     does this mean we do not need to store or compute those values?


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
