package com.skroll.analyzer.model.bn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.NodeTrainingHelper;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.*;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class NaiveBayes {



    DiscreteNode categoryNode;
    List<WordNode> wordNodes;
    List<DiscreteNode>  featureNodes;
    List<DiscreteNode> allDiscreteNodes;

    // for training with complete observed data, we can set observation on all nodes,
    // then make each node update its frequency count

    public NaiveBayes(){

    }

    public NaiveBayes(DiscreteNode categoryNode,
                      List<DiscreteNode> featureNodes,
                      List<WordNode> wordNodes){
        this.categoryNode = categoryNode;
        this.featureNodes = featureNodes;
        this.wordNodes = wordNodes;
        putAllDiscreteNodesInOneList();
    }

    public void putAllDiscreteNodesInOneList(){
        allDiscreteNodes = new ArrayList<>();
        allDiscreteNodes.add(categoryNode);
        allDiscreteNodes.addAll(featureNodes);
    }

    public void setCategoryNode(DiscreteNode categoryNode) {
        this.categoryNode = categoryNode;
    }

    public List<WordNode> getWordNodes() {
        return wordNodes;
    }

    public void setWordNodes(List<WordNode> wordNodes) {
        this.wordNodes = wordNodes;
    }

    public List<DiscreteNode> getFeatureNodes() {
        return featureNodes;
    }

    public void setFeatureNodes(List<DiscreteNode> featureNodes) {
        this.featureNodes = featureNodes;
    }

    public List<DiscreteNode> getAllDiscreteNodes() {
        return allDiscreteNodes;
    }

    public void setAllDiscreteNodes(List<DiscreteNode> allDiscreteNodes) {
        this.allDiscreteNodes = allDiscreteNodes;
    }

    public void setObservation(SimpleDataTuple tuple){
        int[] values = tuple.getDiscreteValues();
        for (int i=0; i<values.length; i++){
            allDiscreteNodes.get(i).setObservation(values[i]);
        }
        for (int i=0; i<wordNodes.size(); i++){
            wordNodes.get(i).setObservation(tuple.getWords());
        }
    }

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

}
