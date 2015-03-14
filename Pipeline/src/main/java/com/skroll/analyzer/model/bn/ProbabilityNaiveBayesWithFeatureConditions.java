package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.bn.node.*;

/**
 * Created by wei2learn on 3/11/2015.
 */
public class ProbabilityNaiveBayesWithFeatureConditions extends NaiveBayesWithFeatureConditions {

    public ProbabilityNaiveBayesWithFeatureConditions(TrainingNaiveBayesWithFeatureConditions tnb) {
        categoryNode = new ProbabilityDiscreteNode((TrainingDiscreteNode) tnb.getCategoryNode());
        TrainingDiscreteNode[] tdfNodeArray = (TrainingDiscreteNode[]) tnb.getDocumentFeatureNodeArray();
        TrainingDiscreteNode[] tfNodeArray = (TrainingDiscreteNode[]) tnb.getFeatureNodeArray();
        featureNodeArray = new ProbabilityDiscreteNode[ tfNodeArray.length];
        documentFeatureNodeArray = new ProbabilityDiscreteNode[ tdfNodeArray.length];
        for (int i=0; i<featureNodeArray.length; i++) {
            featureNodeArray[i] = new ProbabilityDiscreteNode(tfNodeArray[i]);
        }
        for (int i=0; i<documentFeatureNodeArray.length;i++)
            documentFeatureNodeArray[i] = new ProbabilityDiscreteNode( tdfNodeArray[i]);

        wordNode = new ProbabilityWordNode((TrainingWordNode) tnb.getWordNode());
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
    public ProbabilityNaiveBayesWithFeatureConditions(ProbabilityNaiveBayesWithFeatureConditions pnb) {
        categoryNode = new ProbabilityDiscreteNode((ProbabilityDiscreteNode) pnb.getCategoryNode());
        ProbabilityDiscreteNode[] pdfNodeArray = (ProbabilityDiscreteNode[]) pnb.getDocumentFeatureNodeArray();
        ProbabilityDiscreteNode[] pfNodeArray = (ProbabilityDiscreteNode[]) pnb.getFeatureNodeArray();
        documentFeatureNodeArray = new ProbabilityDiscreteNode[ pdfNodeArray.length];
        featureNodeArray = new ProbabilityDiscreteNode[ pfNodeArray.length];
        for (int i=0; i<featureNodeArray.length; i++) {
            featureNodeArray[i] = new ProbabilityDiscreteNode(pfNodeArray[i]);
        }
        for (int i=0; i<documentFeatureNodeArray.length;i++)
            documentFeatureNodeArray[i] = new ProbabilityDiscreteNode( pdfNodeArray[i]);

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
