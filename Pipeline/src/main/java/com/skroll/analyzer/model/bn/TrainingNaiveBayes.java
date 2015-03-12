package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.TrainingDiscreteNode;
import com.skroll.analyzer.model.bn.node.TrainingWordNode;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class TrainingNaiveBayes extends NaiveBayes {


    TrainingDiscreteNode trainingCategoryNode;
    TrainingDiscreteNode[] trainingFeatureNodeArray;

    TrainingDiscreteNode[] trainingDiscreteNodeArray;
    TrainingWordNode trainingWordNode;
    // for training with complete observed data, we can set observation on all nodes,
    // then make each node update its frequency count

    // assuming there is a documentFeature for each feature, so the sizes of the two lists passed in should match.
    public TrainingNaiveBayes(RandomVariableType categoryVar, List<RandomVariableType> featureVarList,
                              List<RandomVariableType> documentFeatureVarList) {
        categoryNode = new TrainingDiscreteNode(Arrays.asList(categoryVar));
        featureNodeArray = new TrainingDiscreteNode[ featureVarList.size()];
        for (int i=0; i<featureVarList.size(); i++) {
            featureNodeArray[i] = new TrainingDiscreteNode(
                    Arrays.asList(featureVarList.get(i), categoryVar));
        }

        wordNode = new TrainingWordNode((TrainingDiscreteNode)categoryNode);
        generateParentsAndChildren();

        // put all nodes in a single array for simpler update.
        int i=0;
        discreteNodeArray[i++] = categoryNode;
        for (DiscreteNode node: featureNodeArray){
            discreteNodeArray[i++] = node;
        }
        trainingDiscreteNodeArray = (TrainingDiscreteNode[]) discreteNodeArray;
        trainingWordNode = (TrainingWordNode) wordNode;
        trainingCategoryNode = (TrainingDiscreteNode) categoryNode;
        trainingFeatureNodeArray = (TrainingDiscreteNode[]) featureNodeArray;

    }

    public void addSample(SimpleDataTuple tuple){
        setObservation(tuple);
        for (DiscreteNode node: discreteNodeArray){
            ((TrainingDiscreteNode) node).updateCount();
        }
        ((TrainingWordNode) wordNode).updateCount();
        clearObservation(); // probably unnecessary
    }
    public TrainingDiscreteNode[] getTrainingDiscreteNodeArray() {
        return trainingDiscreteNodeArray;
    }

    public TrainingDiscreteNode getTrainingCategoryNode() {
        return trainingCategoryNode;
    }

    public TrainingWordNode getTrainingWordNode() {
        return trainingWordNode;
    }

}
