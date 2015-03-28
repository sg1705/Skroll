package com.skroll.analyzer.model.bn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ObjectArrays;
import com.skroll.analyzer.model.bn.node.DiscreteNode;

import java.util.Arrays;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class NaiveBayesWithFeatureConditions extends NaiveBayes{

    DiscreteNode[]  documentFeatureNodeArray;
    DiscreteNode[] featureExistAtDocLevelArray;

    public  NaiveBayesWithFeatureConditions(){

    }

    void generateParentsAndChildren(){
        categoryNode.setChildren( ObjectArrays.concat
                (featureExistAtDocLevelArray, featureNodeArray, DiscreteNode.class));
        categoryNode.setParents(new DiscreteNode[0]);

        for (int i=0; i<documentFeatureNodeArray.length; i++){
            featureExistAtDocLevelArray[i].setParents(Arrays.asList(categoryNode, documentFeatureNodeArray[i]).
                    toArray(new DiscreteNode[2]));
            featureExistAtDocLevelArray[i].setChildren(new DiscreteNode[0]);
            documentFeatureNodeArray[i].setChildren( Arrays.asList( featureExistAtDocLevelArray[i]).
                    toArray( new DiscreteNode[1]));
            documentFeatureNodeArray[i].setParents(new DiscreteNode[0]);
        }
        for (int i=0; i<featureNodeArray.length; i++) {
            featureNodeArray[i].setParents(Arrays.asList(categoryNode).toArray(new DiscreteNode[1]));
            featureNodeArray[i].setChildren(new DiscreteNode[0]);
        }
        wordNode.setParent(categoryNode);
    }

    public void setObservationOfFeatureNodesExistAtDocLevel(int[] values){
        for (int i=0; i<values.length; i++)
            featureExistAtDocLevelArray[i].setObservation(values[i]);
    }

    @JsonIgnore
    public DiscreteNode[] getDocumentFeatureNodeArray() {
        return documentFeatureNodeArray;
    }

    @JsonIgnore
    public DiscreteNode[] getFeatureExistAtDocLevelArray() {
        return featureExistAtDocLevelArray;
    }

    void putAllNodesInOneList(){
        // put all nodes in a single array for simpler update.
        discreteNodeArray = new DiscreteNode[documentFeatureNodeArray.length +
                featureExistAtDocLevelArray.length + featureNodeArray.length+1];
        int i=0;
        discreteNodeArray[i++] = categoryNode;
        for (DiscreteNode node: featureNodeArray){
            discreteNodeArray[i++] = node;
        }

        for (DiscreteNode node: featureExistAtDocLevelArray)
            discreteNodeArray[i++] = node;
        for (DiscreteNode node: documentFeatureNodeArray){
            discreteNodeArray[i++] = node;
        }
    }

    @Override
    public String toString() {
        return "NaiveBayesWithFeatureConditions{" +

                "category=" + categoryNode +
                "\nwordNode=\n" + wordNode +
                "\nfeatureArray=\n" + Arrays.toString(featureNodeArray) +

                "\nfeatureExistAtDocLevelArray=\n" + Arrays.toString(featureExistAtDocLevelArray) +
                ",\n documentFeatureNodeArray=\n" + Arrays.toString(documentFeatureNodeArray) +
                '}';
    }
}
