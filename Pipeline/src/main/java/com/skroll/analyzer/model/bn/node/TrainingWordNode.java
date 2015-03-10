package com.skroll.analyzer.model.bn.node;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * WordNode stores the random variable representing the distribution of words.
 * When it is used, it actually process all words observations in a data tuple.
 * Created by wei2learn on 3/1/2015.
 */
public class TrainingWordNode extends WordNode {
    private static final double PRIOR_COUNT = 100;

    Map<String, double[]> wordCount;

    TrainingWordNode(TrainingDiscreteNode parent){
        super(parent);
        wordCount = parameters;
    }

    public void updateCount(){
        updateCount(1);
    }

    public void updateCount(double weight){
        int parentObservation = parent.getObservation();
        for (String w: observedWords)
            updateCount(w, parentObservation, weight);
    }

    void updateCount(String word, int parentValue){
        updateCount(word, parentValue, 1);
    }

    void updateCount(String word, int parentValue, double weight){
        double[] counts = wordCount.get(word);
        if (counts == null) {
            counts = new double[ parent.getVariable().getFeatureSize() ];
            Arrays.fill(counts, PRIOR_COUNT);
        }
        counts[parentValue] += weight;
    }

    /**
     * convert counts to probabilities
     */
    public Map<String, double[]> getProbabilities(){
        Map<String, double[]> probs = new HashMap<>();
        int numValues = parent.getVariable().getFeatureSize();
        for (String w: wordCount.keySet()){
            double[] p = new double[ parent.getVariable().getFeatureSize()  ];
            double sum=0;
            for (int j=0; j<numValues; j++) sum += wordCount.get(w)[j];
            for (int j=0; j<numValues; j++) p[j] = wordCount.get(w)[j]/sum;
        }
        return probs;
    }

//    double getProbability(String word, int parentValue){
//        return probabilityFunction.get(word) [parentValue];
//    }



    @Override
    public String toString() {
        return "WordNodes{" +
                "wordCount=" + wordCount +
                ", parent=" + parent +
                '}';
    }
}