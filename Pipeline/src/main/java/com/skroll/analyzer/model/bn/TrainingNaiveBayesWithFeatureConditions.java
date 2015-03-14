package com.skroll.analyzer.model.bn;

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

    // assuming the documentFeatures match the front sublist of the features list

    public TrainingNaiveBayesWithFeatureConditions(RandomVariableType categoryVar, List<RandomVariableType> featureVarList,
                                                   List<RandomVariableType> documentFeatureVarList) {

        categoryNode = new TrainingDiscreteNode(Arrays.asList(categoryVar));
        featureNodeArray = new TrainingDiscreteNode[ featureVarList.size()];
        documentFeatureNodeArray = new TrainingDiscreteNode[ documentFeatureVarList.size()];

        for (int i=0; i<featureVarList.size(); i++) {
            if (i>=documentFeatureVarList.size()){
                featureNodeArray[i] = new TrainingDiscreteNode(
                        Arrays.asList(featureVarList.get(i), categoryVar));
                continue;
            }
            documentFeatureNodeArray[i]=new TrainingDiscreteNode( Arrays.asList( documentFeatureVarList.get(i) ));
            featureNodeArray[i] = new TrainingDiscreteNode(
                    Arrays.asList(featureVarList.get(i), categoryVar, documentFeatureVarList.get(i)));
        }

        wordNode = new TrainingWordNode((TrainingDiscreteNode)categoryNode);
        generateParentsAndChildren();

        // put all nodes in a single array for simpler update.
        int i=0;
        discreteNodeArray[i++] = categoryNode;
        for (DiscreteNode node: featureNodeArray){
            discreteNodeArray[i++] = node;
        }
        for (DiscreteNode node: documentFeatureNodeArray){
            discreteNodeArray[i++] = node;
        }

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
