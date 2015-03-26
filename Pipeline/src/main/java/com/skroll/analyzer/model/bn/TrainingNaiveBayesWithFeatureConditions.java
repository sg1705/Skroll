package com.skroll.analyzer.model.bn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.TrainingDiscreteNode;
import com.skroll.analyzer.model.bn.node.TrainingWordNode;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.*;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class TrainingNaiveBayesWithFeatureConditions extends NaiveBayesWithFeatureConditions {


    @JsonCreator
    public TrainingNaiveBayesWithFeatureConditions(
            @JsonProperty("wordNode")TrainingWordNode wordNode,
            @JsonProperty("documentFeatureNodeArray")TrainingDiscreteNode[]  documentFeatureNodeArray,
            @JsonProperty("featureExistAtDocLevelArray")TrainingDiscreteNode[] featureExistAtDocLevelArray,
            @JsonProperty("categoryNode")TrainingDiscreteNode categoryNode,
            @JsonProperty("featureNodeArray") TrainingDiscreteNode[] featureNodeArray,
            @JsonProperty("discreteNodeArray")TrainingDiscreteNode[] discreteNodeArray) {

        this.wordNode = wordNode;
        this.documentFeatureNodeArray = documentFeatureNodeArray;
        this.featureExistAtDocLevelArray = featureExistAtDocLevelArray;
        this.categoryNode = categoryNode;
        this.featureNodeArray = featureNodeArray;
        this.discreteNodeArray = discreteNodeArray;
    }

    // assuming the documentFeatures match the front sublist of the features list

    public TrainingNaiveBayesWithFeatureConditions(RandomVariableType categoryVar,
                                                   List<RandomVariableType> featureVarList,
                                                   List<RandomVariableType> featureExistsAtDocLevelVarList,
                                                   List<RandomVariableType> documentFeatureVarList) {

        categoryNode = new TrainingDiscreteNode(Arrays.asList(categoryVar));
        featureNodeArray = new TrainingDiscreteNode[ featureVarList.size()];
        documentFeatureNodeArray = new TrainingDiscreteNode[ documentFeatureVarList.size()];
        featureExistAtDocLevelArray = new TrainingDiscreteNode[ featureExistsAtDocLevelVarList.size()];

        for (int i=0; i<documentFeatureVarList.size(); i++) {
            documentFeatureNodeArray[i]=new TrainingDiscreteNode( Arrays.asList( documentFeatureVarList.get(i) ));
            featureExistAtDocLevelArray[i] = new TrainingDiscreteNode(
                    Arrays.asList(featureExistsAtDocLevelVarList.get(i), categoryVar, documentFeatureVarList.get(i)));
        }

        for (int i=0; i<featureVarList.size(); i++) {
            featureNodeArray[i] = new TrainingDiscreteNode(
                    Arrays.asList(featureVarList.get(i), categoryVar));
        }

        wordNode = new TrainingWordNode((TrainingDiscreteNode)categoryNode);
        generateParentsAndChildren();

        putAllNodesInOneList();


    }


    public void addSample(SimpleDataTuple tuple){
        setObservation(tuple);
        for (DiscreteNode node: discreteNodeArray){
            ((TrainingDiscreteNode) node).updateCount();
        }
        ((TrainingWordNode) wordNode).updateCount();
        clearObservation(); // probably unnecessary
    }


}
