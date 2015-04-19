package com.skroll.analyzer.model.bn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.*;

/**
 * Created by wei2learn on 1/3/2015.
 */
public abstract class NaiveBayes {


    WordNode[] wordNodeArray;
    DiscreteNode categoryNode;
    DiscreteNode[] featureNodeArray;
    DiscreteNode[] discreteNodeArray;

//    List<WordNode> wordNodes;
//    DiscreteNode categoryNode;
//    List<DiscreteNode>  featureNodes;
//    List<DiscreteNode> allDiscreteNodes;

    // for training with complete observed data, we can set observation on all nodes,
    // then make each node update its frequency count

    public NaiveBayes(){

    }

    // this NaiveBayes is not really instantiated, so may not need this constructor.
    // assuming there is a documentFeature for each feature, so the sizes of the two lists passed in should match.
    public NaiveBayes(RandomVariableType categoryVar,
                      List<RandomVariableType> featureVarList, List<RandomVariableType> wordVarList) {
        wordNodeArray = new WordNode[wordVarList.size()];
        categoryNode = new DiscreteNode(Arrays.asList(categoryVar));
        featureNodeArray = new DiscreteNode[ featureVarList.size()];
        for (int i=0; i<featureVarList.size(); i++) {
            featureNodeArray[i] = new DiscreteNode(
                    Arrays.asList(featureVarList.get(i), categoryVar));
        }

        for (int i=0; i<wordVarList.size(); i++){
            wordNodeArray[i] = new WordNode(categoryNode);
        }

        generateParentsAndChildren();

        // put all nodes in a single array for simpler update.
        int i=0;
        discreteNodeArray[i++] = categoryNode;
        for (DiscreteNode node: featureNodeArray){
            discreteNodeArray[i++] = node;
        }
    }

    void generateParentsAndChildren(){
        categoryNode.setChildren(featureNodeArray);
        categoryNode.setParents(new DiscreteNode[0]);
        for (int i=0; i<featureNodeArray.length; i++){
            featureNodeArray[i].setParents(Arrays.asList(categoryNode).
                    toArray(new DiscreteNode[1]));
            featureNodeArray[i].setChildren(new DiscreteNode[0]);
        }

    }

    void generateWordNodeParents(){
        for (int i=0; i<wordNodeArray.length; i++){
            wordNodeArray[i].setParent(categoryNode);
        }

    }

    public void setObservation(SimpleDataTuple tuple){
        int[] values = tuple.getDiscreteValues();
        for (int i=0; i<values.length; i++){
            discreteNodeArray[i].setObservation(values[i]);
        }
        for (int i=0; i<wordNodeArray.length; i++){
            wordNodeArray[i].setObservation(tuple.getWords(i));
        }
    }

    public void clearObservation(){
        for (DiscreteNode node:discreteNodeArray){
            node.clearObservation();
        }
        for (int i=0; i<wordNodeArray.length; i++){
            wordNodeArray[i].clearObservation();
        }
    }

    @JsonIgnore
    public WordNode[] getWordNodeArray() {
        return wordNodeArray;
    }

    public DiscreteNode[] getDiscreteNodeArray() {
        return discreteNodeArray;
    }

    @JsonIgnore
    public DiscreteNode getCategoryNode() {
        return categoryNode;
    }

    @JsonIgnore
    public DiscreteNode[] getFeatureNodeArray() {
        return featureNodeArray;
    }

}
