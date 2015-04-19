package com.skroll.analyzer.model.bn.node;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.eclipse.persistence.internal.jaxb.many.MapEntry;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * WordNode stores the random variable representing the distribution of words.
 * When it is used, it actually process all words observations in a data tuple.
 * Created by wei2learn on 3/1/2015.
 */

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = com.skroll.analyzer.model.bn.node.LogProbabilityWordNode.class, name = "LogProbabilityWordNode"),
        @JsonSubTypes.Type(value = com.skroll.analyzer.model.bn.node.ProbabilityWordNode.class, name = "ProbabilityWordNode"),
        @JsonSubTypes.Type(value = com.skroll.analyzer.model.bn.node.TrainingWordNode.class, name = "TrainingWordNode")})
public class WordNode                                                                                                                                                       {

    @JsonProperty("parameters")
    Map<String, double[]> parameters = new HashMap<>();
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
    public WordNode(){
    }

    String mapToString(){
        String s="";
        for (Map.Entry<String, double[]> e: parameters.entrySet()){
            s+=e.getKey()+ "=" + Arrays.toString( e.getValue() )+" ";
        }
        return s;
    }

    public void setParent(DiscreteNode parent) {
        this.parent = parent;
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
                ", parent=" + parent +
                '}';
    }
}
