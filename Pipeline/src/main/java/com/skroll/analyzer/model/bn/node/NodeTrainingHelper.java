package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.inference.BNInference;

import java.util.*;

/**
 * Created by wei on 4/11/15.
 */

// It is okay for these helper method to use nodes parameters
public class NodeTrainingHelper {
    public static final double PRIOR_COUNT = .0001;

    /**
     * Creates a DiscreteNode for training from a list of RandomVariables in the node's family and its parent nodes.
     * The first entry of the randomVariables is the variable of the current node.
     * The parent nodes correspond to the random variables after the first entry.
     * @param randomVariables   the random variables in the family
     * @param parents   the parent nodes
     * @return  a new DiscreteNode for training
     */
    public static DiscreteNode createTrainingDiscreteNode(List<RandomVariable> randomVariables,
                                                          List<DiscreteNode> parents){
        DiscreteNode node = new DiscreteNode(parents.toArray(new DiscreteNode[parents.size()]));
        RandomVariable[] variables =
                randomVariables.toArray(new RandomVariable[randomVariables.size()]);
        node.setFamilyVariables( variables);
        int totalSize = NodeHelper.sizeUpTo(node, randomVariables.size());
        double[] parameters = new double[totalSize];
        Arrays.fill(parameters, PRIOR_COUNT);
        node.setParameters(parameters);
        return node;

    }

    public static DiscreteNode createTrainingDiscreteNode(List<RandomVariable> randomVariables) {
        return createTrainingDiscreteNode(randomVariables, new ArrayList<DiscreteNode>());
    }


    static int getIndex(RandomVariable[] variables, int[] multiIndex) { // least significant digit on the left.
        int index=0;
        for (int i=multiIndex.length-1; i>=0; i--){
            index *= variables[i].getFeatureSize();
            index += multiIndex[i];
        }
        return index;
    }

    public static void updateCount(DiscreteNode node){
        updateCount(node, 1);
    }

    public static void updateCount(DiscreteNode node, double weight){
        if (node.getObservation() == -1) return; // skip unobserved nodes

        RandomVariable[] variables = node.getFamilyVariables();
        int[] multiIndex= new int[variables.length];
        DiscreteNode[] parents = node.getParents();

        // the node and parents must be observed.
        multiIndex[0] = node.getObservation();

        for (int i=0; i< parents.length;i++)
            multiIndex[i+1] = parents[i].getObservation();

        double[] parameters = node.getParameters();
        parameters[ getIndex(node.getFamilyVariables(), multiIndex)] += weight;

    }

    public static double[] getLogProbabilities(DiscreteNode trainingNode){

        double[] probs = trainingNode.copyOfParameters();

        int numValues = trainingNode.numValues();
        for (int i=0; i< probs.length; i+=numValues){
            double sum=0;
            for (int j=0; j<numValues; j++) sum += probs[j+i];
            for (int j=0; j<numValues; j++) probs[i+j] = Math.log(probs[i+j]/sum);
        }
        return probs;
    }


    /**
     * MutiplexNode should have at least 2 parents, so the randomVariables size should be at least 3,
     * and parents size should be at least 2.
     *
     * @param randomVariables
     * @param selectingNode
     * @return
     */
    public static MultiplexNode createTrainingMultiplexNode(List<RandomVariable> randomVariables,
                                                            DiscreteNode selectingNode,
                                                            List<DiscreteNode> parents) {
        MultiplexNode multiNode = new MultiplexNode(selectingNode);
        DiscreteNode[] nodes = new DiscreteNode[selectingNode.numValues()];

        // first node represent none. It has no parents.
//        nodes[0] = createTrainingDiscreteNode(Arrays.asList(randomVariables.get(0)));
        for (int n = 0; n < nodes.length; n++) {
            nodes[n] = createTrainingDiscreteNode(
                    // the second family variable starts at index 2 to skip the node var and the category var.
                    Arrays.asList(randomVariables.get(0), randomVariables.get(n + 2)), Arrays.asList(parents.get(n)));
        }
        multiNode.setNodes(nodes);

        return multiNode;

    }

    public static void updateCount(MultiplexNode multiNode) {
        updateCount(multiNode.getActiveNode(), 1);
    }

    public static void updateCount(MultiplexNode multiNode, double weight) {
        updateCount(multiNode.getActiveNode(), weight);
    }



    public static WordNode createTrainingWordNode(DiscreteNode parent){
        return new WordNode(parent);

    }

    public static void updateCount(WordNode node){
        updateCount(node, 1);
    }

    public static void updateCount(WordNode node, double weight){
        String[] observedWords = node.getObservation();
        for (String w: observedWords)
            updateCount(node, w, weight);
    }

    static void updateCount(WordNode node, String word, double weight){
        Map<String, double[]> parameters = node.getParameters();
        double[] counts = parameters.get(word);
        if (counts == null) {
            counts = new double[ node.getParent().getVariable().getFeatureSize() ];
            //Arrays.fill(parameters, PRIOR_COUNT);
            parameters.put(word, counts);
        }
        counts[ node.getParent().getObservation() ] += weight;
    }

    public static Map<String, double[]> getLogProbabilities(WordNode trainingNode){
        Map<String, double[]> probs = new HashMap<>();
        Map<String, double[]> counts = trainingNode.getParameters();
        DiscreteNode parent = trainingNode.getParent();
        int numValues = parent.getVariable().getFeatureSize();

        double[] sum = new double[numValues];
        Arrays.fill(sum, PRIOR_COUNT * counts.values().size());
        for (double[] countsForWord : counts.values()) {
            for (int i = 0; i < numValues; i++) {
                sum[i] += countsForWord[i];
            }
        }

        double[] logSums = Arrays.stream(sum).map(v -> Math.log(v)).toArray();

        for (String w : counts.keySet()) {
            double[] p = counts.get(w).clone();
            double[] logP = new double[numValues];
            for (int i = 0; i < numValues; i++) {
                logP[i] = Math.log(p[i] + PRIOR_COUNT) - logSums[i];
            }
            probs.put(w, logP);
        }

//        //Experiment next line uncommented
//        double [] priorCounts = BNInference.normalize(parent.getParameters(), PRIOR_COUNT);
//        for (String w: counts.keySet()){
//            double[] p = new double[ parent.getVariable().getFeatureSize()  ];
//            //double sum=0;
//            //for (int j=0; j<numValues; j++) sum += parameters.get(w)[j];
//
//
//            //hack for testing purpose
////            if (counts.get(w)[0]+ counts.get(w)[1] <1) continue;
////          Next 2 lines commented for experiment
////           for (int j=0; j<numValues; j++) p[j] = Math.log((0.01 +
////                    counts.get(w)[j])/ parent.getParameter(j));
////          Next 2 lines experiment code
//           for (int j=0; j<numValues; j++) p[j] = Math.log((priorCounts[j] +
//                    counts.get(w)[j])/ parent.getParameter(j));
//            probs.put(w,p);
//        }
        return probs;
    }
}
