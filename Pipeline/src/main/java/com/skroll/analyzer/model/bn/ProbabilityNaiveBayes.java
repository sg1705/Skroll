package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class ProbabilityNaiveBayes extends NaiveBayes{

    // build from TrainingNaiveBayes
    public ProbabilityNaiveBayes(TrainingNaiveBayes tnb) {
        categoryNode = new ProbabilityDiscreteNode(tnb.getTrainingCategoryNode());
        TrainingDiscreteNode[] trainingFreatureNodeArray = (TrainingDiscreteNode[]) tnb.getFeatureNodeArray();
        featureNodeArray = new ProbabilityDiscreteNode[ trainingFreatureNodeArray.length];
        for (int i=0; i<featureNodeArray.length; i++) {
            featureNodeArray[i] = new ProbabilityDiscreteNode(trainingFreatureNodeArray[i]);
        }

        wordNode = new ProbabilityWordNode(tnb.getTrainingWordNode());
        generateParentsAndChildren();

        // put all nodes in a single array for simpler update.
        int i=0;
        discreteNodeArray[i++] = categoryNode;
        for (DiscreteNode node: featureNodeArray){
            discreteNodeArray[i++] = node;
        }
    }

    /**
     * copy constructor
     */
    public ProbabilityNaiveBayes(ProbabilityNaiveBayes pnb) {
        categoryNode = new ProbabilityDiscreteNode((ProbabilityDiscreteNode) pnb.getCategoryNode());
        ProbabilityDiscreteNode[] probabilityFreatureNodeArray = (ProbabilityDiscreteNode[]) pnb.getFeatureNodeArray();
        featureNodeArray = new ProbabilityDiscreteNode[ probabilityFreatureNodeArray.length];
        for (int i=0; i<featureNodeArray.length; i++) {
            featureNodeArray[i] = new ProbabilityDiscreteNode(probabilityFreatureNodeArray[i]);
        }

        wordNode = new ProbabilityWordNode((ProbabilityWordNode) pnb.getWordNode());
        generateParentsAndChildren();

        // put all nodes in a single array for simpler update.
        int i=0;
        discreteNodeArray[i++] = categoryNode;
        for (DiscreteNode node: featureNodeArray){
            discreteNodeArray[i++] = node;
        }
    }

}
