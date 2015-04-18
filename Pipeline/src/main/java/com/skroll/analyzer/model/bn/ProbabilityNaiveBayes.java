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
        categoryNode = new ProbabilityDiscreteNode((TrainingDiscreteNode)tnb.getCategoryNode());
        TrainingDiscreteNode[] trainingFreatureNodeArray = (TrainingDiscreteNode[]) tnb.getFeatureNodeArray();
        featureNodeArray = new ProbabilityDiscreteNode[ trainingFreatureNodeArray.length];
        for (int i=0; i<featureNodeArray.length; i++) {
            featureNodeArray[i] = new ProbabilityDiscreteNode(trainingFreatureNodeArray[i]);
        }

        TrainingWordNode[] trainingWordNodes = (TrainingWordNode[]) tnb.getWordNodeArray();
        wordNodeArray = new ProbabilityWordNode[trainingWordNodes.length];
        for (int i=0; i<trainingWordNodes.length; i++)
            wordNodeArray[i] = new ProbabilityWordNode(trainingWordNodes[i]);

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

        ProbabilityWordNode[] oldNodes = (ProbabilityWordNode[]) pnb.getWordNodeArray();
        wordNodeArray = new ProbabilityWordNode[oldNodes.length];
        for (int i=0; i<oldNodes.length; i++)
            wordNodeArray[i] = new ProbabilityWordNode(oldNodes[i]);

        generateParentsAndChildren();

        // put all nodes in a single array for simpler update.
        int i=0;
        discreteNodeArray[i++] = categoryNode;
        for (DiscreteNode node: featureNodeArray){
            discreteNodeArray[i++] = node;
        }
    }

}
