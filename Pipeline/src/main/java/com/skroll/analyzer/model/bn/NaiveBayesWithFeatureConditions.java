package com.skroll.analyzer.model.bn;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ObjectArrays;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class NaiveBayesWithFeatureConditions extends NaiveBayes{

//    DiscreteNode[]  documentFeatureNodeArray;
//    DiscreteNode[] featureExistAtDocLevelArray;
    List<DiscreteNode> documentFeatureNodes;
    List<DiscreteNode> featureExistAtDocLevelNodes;



    public NaiveBayesWithFeatureConditions(DiscreteNode categoryNode,
                                           List<DiscreteNode> featureNodes,
                                           List<DiscreteNode> featureExistAtDocLevelNodes,
                                           List<DiscreteNode> docFeatureNodes,
                                           List<WordNode> wordNodes){
        this.categoryNode = categoryNode;
        this.featureNodes = featureNodes;
        this.featureExistAtDocLevelNodes = featureExistAtDocLevelNodes;
        this.documentFeatureNodes = docFeatureNodes;
        this.wordNodes = wordNodes;
        putAllDiscreteNodesInOneList();
    }


    public void putAllDiscreteNodesInOneList(){
        super.putAllDiscreteNodesInOneList();
        allDiscreteNodes.addAll(featureExistAtDocLevelNodes);
        allDiscreteNodes.addAll(documentFeatureNodes);
    }

    public void setObservationOfFeatureNodesExistAtDocLevel(int[] values){
        for (int i=0; i<values.length; i++)
            featureExistAtDocLevelNodes.get(i).setObservation(values[i]);
    }


    public List<DiscreteNode> getDocumentFeatureNodes() {
        return documentFeatureNodes;
    }

    public void setDocumentFeatureNodes(List<DiscreteNode> documentFeatureNodes) {
        this.documentFeatureNodes = documentFeatureNodes;
    }

    public List<DiscreteNode> getFeatureExistAtDocLevelNodes() {
        return featureExistAtDocLevelNodes;
    }

    public void setFeatureExistAtDocLevelNodes(List<DiscreteNode> featureExistAtDocLevelNodes) {
        this.featureExistAtDocLevelNodes = featureExistAtDocLevelNodes;
    }

    @Override
    public String toString() {
        return "NaiveBayesWithFeatureConditions{" +

                "category=" + categoryNode +
                "\nwordNode=\n" + wordNodes +
                "\nfeatureArray=\n" + featureNodes +

                "NaiveBayesWithFeatureConditions{" +
                "documentFeatureNodes=" + documentFeatureNodes +
                ", featureExistAtDocLevelNodes=" + featureExistAtDocLevelNodes +
                '}';
    }


}
