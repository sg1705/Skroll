package com.skroll.analyzer.model.bn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.bn.node.MultiplexNode;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.List;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class NaiveBayesWithMultiNodes extends NaiveBayes {

    @JsonProperty("documentFeaturesNodes")
//    List<DiscreteNode> documentFeatureNodes;
            List<List<DiscreteNode>> documentFeatureNodes; //indexed by feature number, category number.
    @JsonProperty("multiNodes")
    List<MultiplexNode> multiNodes;


    @JsonCreator
    public NaiveBayesWithMultiNodes(
            @JsonProperty("categoryNode") DiscreteNode categoryNode,
            @JsonProperty("featureNodes") List<DiscreteNode> featureNodes,
            @JsonProperty("multiNodes") List<MultiplexNode> multiNodes,
            @JsonProperty("documentFeaturesNodes") List<List<DiscreteNode>> docFeatureNodes,
            @JsonProperty("wordNodes") List<WordNode> wordNodes) {
        this.categoryNode = categoryNode;
        this.featureNodes = featureNodes;
        this.multiNodes = multiNodes;
//        this.documentFeatureNodes = docFeatureNodes;
        this.documentFeatureNodes = docFeatureNodes;
        this.wordNodes = wordNodes;
        putAllDiscreteNodesInOneList();
    }


    @JsonIgnore
    public void setMultiNodesObservation(int[] values) {
        for (int i = 0; i < values.length; i++)
            multiNodes.get(i).setObservation(values[i]);
    }

    @JsonIgnore
    public void setParaFeatureObservation(int[] values) {
        for (int i = 0; i < values.length; i++)
            featureNodes.get(i).setObservation(values[i]);
    }

    @JsonIgnore
    public void setDocFeatureObservation(int[][] values) {
        for (int i = 0; i < values.length; i++)
            for (int c = 0; c < values[0].length; c++)
                documentFeatureNodes.get(i).get(c).setObservation(values[i][c]);
    }


    @JsonIgnore
    public void setObservation(NBMNTuple tuple) {
        setParaFeatureObservation(tuple.getFeatureValues());
        setMultiNodesObservation(tuple.getMultiNodeValues());
        setDocFeatureObservation(tuple.getDocFeatureValues());
        setWordsObservation(tuple.getWordsList());
    }


    @JsonIgnore
    public void setWordsObservation(List<String[]> wordsList) {
        for (int i = 0; i < wordsList.size(); i++)
            wordNodes.get(i).setObservation(wordsList.get(i));
    }

    @JsonIgnore
    public List<List<DiscreteNode>> getDocumentFeatureNodes() {
        return documentFeatureNodes;
    }

    @JsonIgnore
    public void setDocumentFeatureNodes(List<List<DiscreteNode>> documentFeatureNodes) {
        this.documentFeatureNodes = documentFeatureNodes;
    }

    @JsonIgnore
    public List<MultiplexNode> getMultiNodes() {
        return multiNodes;
    }

    @JsonIgnore
    public void setMultiNodes(List<MultiplexNode> multiNodes) {
        this.multiNodes = multiNodes;
    }

    @Override
    @JsonIgnore
    public String toString() {
        return "NaiveBayesWithMultiplexNodes{" +

                "category=" + categoryNode +
                "\nwordNode=\n" + wordNodes +
                "\nfeatureArray=\n" + featureNodes +

                "NaiveBayesWithFeatureConditions{" +
                "documentFeatureNodes=" + documentFeatureNodes +
                ", featureExistAtDocLevelNodes=" + multiNodes +
                '}';
    }

    public boolean equals(NaiveBayesWithMultiNodes nb) {
        boolean isEquals = true;
        for (int i = 0; i < documentFeatureNodes.size(); i++)
            isEquals = isEquals && DiscreteNode.compareDNList(
                    this.documentFeatureNodes.get(i), nb.documentFeatureNodes.get(i));
        return isEquals;
    }


}
