package com.skroll.analyzer.model.bn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.NodeTrainingHelper;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.*;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class NaiveBayes {


    @JsonProperty("categoryNode")
    DiscreteNode categoryNode;
    @JsonProperty("wordNodes")
    List<WordNode> wordNodes;
    @JsonProperty("featureNodes")
    List<DiscreteNode>  featureNodes;


    List<DiscreteNode> allDiscreteNodes;

    // for training with complete observed data, we can set observation on all nodes,
    // then make each node update its frequency count

    public NaiveBayes(){

    }

    @JsonCreator
    public NaiveBayes(@JsonProperty("categoryNode")DiscreteNode categoryNode,
                      @JsonProperty("featureNodes")List<DiscreteNode> featureNodes,
                      @JsonProperty("wordNodes")List<WordNode> wordNodes){
        this.categoryNode = categoryNode;
        this.featureNodes = featureNodes;
        this.wordNodes = wordNodes;
        putAllDiscreteNodesInOneList();
    }

    @JsonIgnore
    public void putAllDiscreteNodesInOneList(){
        allDiscreteNodes = new ArrayList<>();
        allDiscreteNodes.add(categoryNode);
        allDiscreteNodes.addAll(featureNodes);
    }

    @JsonIgnore
    public void setCategoryNode(DiscreteNode categoryNode) {
        this.categoryNode = categoryNode;
    }

    @JsonIgnore
    public List<WordNode> getWordNodes() {
        return wordNodes;
    }

    @JsonIgnore
    public void setWordNodes(List<WordNode> wordNodes) {
        this.wordNodes = wordNodes;
    }

    @JsonIgnore
    public List<DiscreteNode> getFeatureNodes() {
        return featureNodes;
    }

    @JsonIgnore
    public void setFeatureNodes(List<DiscreteNode> featureNodes) {
        this.featureNodes = featureNodes;
    }

    @JsonIgnore
    public List<DiscreteNode> getAllDiscreteNodes() {
        return allDiscreteNodes;
    }

    @JsonIgnore
    public void setAllDiscreteNodes(List<DiscreteNode> allDiscreteNodes) {
        this.allDiscreteNodes = allDiscreteNodes;
    }

    @JsonIgnore
    public void setObservation(SimpleDataTuple tuple){
        int[] values = tuple.getDiscreteValues();
        for (int i=0; i<values.length; i++){
            allDiscreteNodes.get(i).setObservation(values[i]);
        }
        for (int i=0; i<wordNodes.size(); i++){
            wordNodes.get(i).setObservation(tuple.getWords(i));
        }
    }

    @JsonIgnore
    public void clearObservation(){
        for (DiscreteNode node: allDiscreteNodes){
            node.clearObservation();
        }
        for (WordNode node: wordNodes) {
            node.clearObservation();
        }
    }

    @JsonIgnore
    public DiscreteNode getCategoryNode() {
        return categoryNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NaiveBayes that = (NaiveBayes) o;

        if (!getCategoryNode().equals(that.getCategoryNode())) return false;
        //todo: word node is harder to test for equality because of the parameter map contains array as values
        // should probably implement manual test.
//        if (!getWordNodes().equals(that.getWordNodes())) return false;
        return DiscreteNode.compareDNList(getFeatureNodes(), that.getFeatureNodes());

    }

    @Override
    public int hashCode() {
        int result = getCategoryNode().hashCode();
        result = 31 * result + getWordNodes().hashCode();
        result = 31 * result + getFeatureNodes().hashCode();
        return result;
    }
}
