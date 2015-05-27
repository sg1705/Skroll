package com.skroll.analyzer.model.bn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ObjectArrays;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class NaiveBayesWithFeatureConditions extends NaiveBayes{

    @JsonProperty("documentFeaturesNodes")
    List<DiscreteNode> documentFeatureNodes;
    @JsonProperty("featureExistAtDocLevelNodes")
    List<DiscreteNode> featureExistAtDocLevelNodes;



    @JsonCreator
    public NaiveBayesWithFeatureConditions(
            @JsonProperty("categoryNode")DiscreteNode categoryNode,
            @JsonProperty("featureNodes")List<DiscreteNode> featureNodes,
            @JsonProperty("featureExistAtDocLevelNodes")List<DiscreteNode> featureExistAtDocLevelNodes,
            @JsonProperty("docFeatureNodes")List<DiscreteNode> docFeatureNodes,
            @JsonProperty("wordNodes")List<WordNode> wordNodes){
        this.categoryNode = categoryNode;
        this.featureNodes = featureNodes;
        this.featureExistAtDocLevelNodes = featureExistAtDocLevelNodes;
        this.documentFeatureNodes = docFeatureNodes;
        this.wordNodes = wordNodes;
        putAllDiscreteNodesInOneList();
    }


    @JsonIgnore
    public void putAllDiscreteNodesInOneList(){
        super.putAllDiscreteNodesInOneList();
        allDiscreteNodes.addAll(featureExistAtDocLevelNodes);
        allDiscreteNodes.addAll(documentFeatureNodes);
    }

    @JsonIgnore
    public void setObservationOfFeatureNodesExistAtDocLevel(int[] values){
        for (int i=0; i<values.length; i++)
            featureExistAtDocLevelNodes.get(i).setObservation(values[i]);
    }

    @JsonIgnore
    public void setParaFeatureObservation(int[] values) {
        for (int i = 0; i < values.length; i++)
            featureNodes.get(i).setObservation(values[i]);
    }

    @JsonIgnore
    public List<DiscreteNode> getDocumentFeatureNodes() {
        return documentFeatureNodes;
    }

    @JsonIgnore
    public void setDocumentFeatureNodes(List<DiscreteNode> documentFeatureNodes) {
        this.documentFeatureNodes = documentFeatureNodes;
    }

    @JsonIgnore
    public List<DiscreteNode> getFeatureExistAtDocLevelNodes() {
        return featureExistAtDocLevelNodes;
    }

    @JsonIgnore
    public void setFeatureExistAtDocLevelNodes(List<DiscreteNode> featureExistAtDocLevelNodes) {
        this.featureExistAtDocLevelNodes = featureExistAtDocLevelNodes;
    }

    @Override
    @JsonIgnore
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
