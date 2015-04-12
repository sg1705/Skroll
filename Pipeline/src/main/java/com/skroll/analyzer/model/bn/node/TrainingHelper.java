package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariableType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wei on 4/11/15.
 */

// It is okay for these helper method to use nodes parameters
public class TrainingHelper {
    private static final double PRIOR_COUNT = .1;

    static DiscreteNode createTrainingDiscreteNode(List<RandomVariableType> randomVariables){

        DiscreteNode node = new DiscreteNode();
        RandomVariableType[] variables =
                randomVariables.toArray(new RandomVariableType[randomVariables.size()]);
        node.setFamilyVariables( variables);
        int totalSize = node.sizeUpTo(randomVariables.size());
        double[] parameters = new double[totalSize];
        Arrays.fill(parameters, PRIOR_COUNT);
        node.setParameters(parameters);
        return node;

    }



    static int getIndex(RandomVariableType[] variables, int [] multiIndex){ // least significant digit on the left.
        int index=0;
        for (int i=multiIndex.length-1; i>=0; i--){
            index *= variables[i].getFeatureSize();
            index += multiIndex[i];
        }
        return index;
    }

    static void updateCount(DiscreteNode node){
        updateCount(node, 1);
    }

    static void updateCount(DiscreteNode node, double weight){

        RandomVariableType[] variables = node.getFamilyVariables();
        int[] multiIndex= new int[variables.length];
        DiscreteNode[] parents = node.getParents();
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




    public static WordNode createTrainingWordNode(DiscreteNode parent){
        WordNode node = new WordNode();
        node.setParent(parent);
        return node;
    }

    static void updateCount(WordNode node){
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
        //double [] priorCounts = ((TrainingDiscreteNode) parent).getPriorCount(PRIOR_COUNT);
        Map<String, double[]> probs = new HashMap<>();
        Map<String, double[]> counts = trainingNode.getParameters();
        DiscreteNode parent = trainingNode.getParent();
        int numValues = parent.getVariable().getFeatureSize();


        for (String w: counts.keySet()){
            double[] p = new double[ parent.getVariable().getFeatureSize()  ];
            //double sum=0;
            //for (int j=0; j<numValues; j++) sum += parameters.get(w)[j];


            //hack for testing purpose
            if (counts.get(w)[0]+ counts.get(w)[1] <1) continue;
            for (int j=0; j<numValues; j++) p[j] = Math.log((0.01 +
                    counts.get(w)[j])/ parent.getParameter(j));

//            for (int j=0; j<numValues; j++) p[j] = Math.log((priorCounts[j] +
//                    parameters.get(w)[j])/ parent.getParameter(j));
            probs.put(w,p);
        }
        return probs;
    }
}
