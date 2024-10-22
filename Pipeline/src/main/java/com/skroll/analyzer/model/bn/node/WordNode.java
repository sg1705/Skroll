package com.skroll.analyzer.model.bn.node;

import com.fasterxml.jackson.annotation.*;
import com.skroll.analyzer.model.RandomVariable;
import org.eclipse.persistence.internal.jaxb.many.MapEntry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * WordNode stores the random variable representing the distribution of words.
 * When it is used, it actually process all words observations in a data tuple.
 * Created by wei2learn on 3/1/2015.
 */

public class WordNode                                                                                                                                                       {

    @JsonProperty("parameters")
    Map<String, double[]> parameters = new HashMap<>();
    @JsonProperty("parent")
    DiscreteNode parent;

    String[] observedWords = new String[0];

    @JsonIgnore
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

    @JsonCreator
    public WordNode( @JsonProperty("parent")DiscreteNode parent,
                     @JsonProperty("parameters")Map<String, double[]> parameters,
                     @JsonProperty("observedWords")String[] observedWords){
        this.parent = parent;
        this.parameters = parameters;
        this.observedWords = observedWords;
    }


    String mapToString(){
        String s="";
        for (Map.Entry<String, double[]> e: parameters.entrySet()){
            s+=e.getKey()+ "=" + Arrays.toString( e.getValue() )+" ";
        }
        return s;
    }

    public DiscreteNode getParent() {
        return parent;
    }

    public void setParent(DiscreteNode parent) {
        this.parent = parent;
    }

    public Map<String, double[]> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, double[]> parameters) {
        this.parameters = parameters;
    }

    public Map<String, double[]> getCopyOfParameters() {
        Map<String, double[]> newPara = new HashMap<>();
        newPara.putAll(parameters);
        return parameters;
    }

    @Override
    public String toString() {
        return "WordNode{" +
                "parameters=" + mapToString() +
                ", parent=" + parent.getVariable() +
                '}';
    }


//  todo: the parameters Map equals method doesn't work directly because it has arrays used for values in the map. need to implement manual check through all entry.
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        WordNode wordNode = (WordNode) o;
//
//        if (!getParameters().equals(wordNode.getParameters())) return false;
//        if (!getParent().equals(wordNode.getParent())) return false;
//        // Probably incorrect - comparing Object[] arrays with Arrays.equals
//        return Arrays.equals(observedWords, wordNode.observedWords);
//
//    }
//
//    @Override
//    public int hashCode() {
//        int result = getParameters().hashCode();
//        result = 31 * result + getParent().hashCode();
//        result = 31 * result + Arrays.hashCode(observedWords);
//        return result;
//    }
}
