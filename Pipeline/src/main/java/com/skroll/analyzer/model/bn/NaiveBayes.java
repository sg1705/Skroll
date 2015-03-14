package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.*;

/**
 * Created by wei2learn on 1/3/2015.
 */
public abstract class NaiveBayes {

    WordNode wordNode;
    DiscreteNode categoryNode;
    DiscreteNode[] featureNodeArray;
    DiscreteNode[] discreteNodeArray;

    // for training with complete observed data, we can set observation on all nodes,
    // then make each node update its frequency count

    public NaiveBayes(){

    }

    // this NaiveBayes is not really instantiated, so may not need this constructor.
    // assuming there is a documentFeature for each feature, so the sizes of the two lists passed in should match.
    public NaiveBayes(RandomVariableType categoryVar, List<RandomVariableType> featureVarList) {
        categoryNode = new DiscreteNode(Arrays.asList(categoryVar));
        featureNodeArray = new DiscreteNode[ featureVarList.size()];
        for (int i=0; i<featureVarList.size(); i++) {
            featureNodeArray[i] = new DiscreteNode(
                    Arrays.asList(featureVarList.get(i), categoryVar));
        }

        wordNode = new WordNode(categoryNode);
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
        for (int i=0; i<featureNodeArray.length; i++){
            featureNodeArray[i].setParents(Arrays.asList(categoryNode).
                    toArray(new DiscreteNode[1]));
        }
    }

    public void setObservation(SimpleDataTuple tuple){
        int[] values = tuple.getDiscreteValues();
        for (int i=0; i<values.length; i++){
            discreteNodeArray[i].setObservation(values[i]);
        }
        wordNode.setObservation( tuple.getWords());
    }

    public void clearObservation(){
        for (DiscreteNode node:discreteNodeArray){
            node.clearObservation();
        }
        wordNode.clearObservation();
    }

    public WordNode getWordNode() {
        return wordNode;
    }

    public DiscreteNode getCategoryNode() {
        return categoryNode;
    }

    public DiscreteNode[] getFeatureNodeArray() {
        return featureNodeArray;
    }

}
