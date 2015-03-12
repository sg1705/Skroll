package com.skroll.analyzer.model.bn.node;

import java.util.HashMap;
import java.util.Map;

/**
 * WordNode stores the random variable representing the distribution of words.
 * When it is used, it actually process all words observations in a data tuple.
 * Created by wei2learn on 3/1/2015.
 */
public class WordNode                                                                                                                                                       {

    Map<String, double[]> parameters = new HashMap<>();
    DiscreteNode parent;

    String[] observedWords = new String[0];

    public String[] getObservation() {
        return observedWords;
    }

    public void setObservation(String[] observedWords) {
        this.observedWords = observedWords;
    }

    public void clearObservation() {
        this.observedWords = new String[0];
    }
    /**
     * Constructor requires a parent node already exists
     */

    public WordNode(DiscreteNode parent){
        this.parent = parent;
    }
    public WordNode(){
    }

    @Override
    public String toString() {
        return "WordNodes{" +
                "parameters=" + parameters +
                ", parent=" + parent +
                '}';
    }
}
