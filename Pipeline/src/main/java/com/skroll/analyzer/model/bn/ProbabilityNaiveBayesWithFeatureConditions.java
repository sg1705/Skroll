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
        TrainingDiscreteNode[] tfedNodeArray = (TrainingDiscreteNode[]) tnb.getFeatureExistAtDocLevelArray();

        featureNodeArray = new ProbabilityDiscreteNode[ tfNodeArray.length];
        documentFeatureNodeArray = new ProbabilityDiscreteNode[ tdfNodeArray.length];
        featureExistAtDocLevelArray = new ProbabilityDiscreteNode[ tfedNodeArray.length];
        for (int i=0; i<featureNodeArray.length; i++) {
            featureNodeArray[i] = new ProbabilityDiscreteNode(tfNodeArray[i]);
        }
        for (int i=0; i<documentFeatureNodeArray.length;i++)
            documentFeatureNodeArray[i] = new ProbabilityDiscreteNode( tdfNodeArray[i]);
        for (int i=0; i<featureExistAtDocLevelArray.length;i++)
            featureExistAtDocLevelArray[i] = new ProbabilityDiscreteNode( tfedNodeArray[i]);

        TrainingWordNode[] trainingWordNodes = (TrainingWordNode[]) tnb.getWordNodeArray();
        wordNodeArray = new ProbabilityWordNode[trainingWordNodes.length];
        for (int i=0; i<trainingWordNodes.length; i++)
            wordNodeArray[i] = new ProbabilityWordNode(trainingWordNodes[i]);

        generateParentsAndChildren();
        putAllNodesInOneList();
    }

    /**
     * copy constructor
     */
    public ProbabilityNaiveBayesWithFeatureConditions(ProbabilityNaiveBayesWithFeatureConditions pnb) {
        categoryNode = new ProbabilityDiscreteNode((ProbabilityDiscreteNode) pnb.getCategoryNode());
        ProbabilityDiscreteNode[] pdfNodeArray = (ProbabilityDiscreteNode[]) pnb.getDocumentFeatureNodeArray();
        ProbabilityDiscreteNode[] pfNodeArray = (ProbabilityDiscreteNode[]) pnb.getFeatureNodeArray();
        TrainingDiscreteNode[] pfedNodeArray = (TrainingDiscreteNode[]) pnb.getFeatureExistAtDocLevelArray();

        documentFeatureNodeArray = new ProbabilityDiscreteNode[ pdfNodeArray.length];
        featureNodeArray = new ProbabilityDiscreteNode[ pfNodeArray.length];
        featureExistAtDocLevelArray = new ProbabilityDiscreteNode[ pfedNodeArray.length];

        for (int i=0; i<featureNodeArray.length; i++) {
            featureNodeArray[i] = new ProbabilityDiscreteNode(pfNodeArray[i]);
        }
        for (int i=0; i<documentFeatureNodeArray.length;i++)
            documentFeatureNodeArray[i] = new ProbabilityDiscreteNode( pdfNodeArray[i]);
        for (int i=0; i<featureExistAtDocLevelArray.length;i++)
            featureExistAtDocLevelArray[i] = new ProbabilityDiscreteNode( pfedNodeArray[i]);


        ProbabilityWordNode[] oldNodes = (ProbabilityWordNode[]) pnb.getWordNodeArray();
        wordNodeArray = new ProbabilityWordNode[oldNodes.length];
        for (int i=0; i<oldNodes.length; i++)
            wordNodeArray[i] = new ProbabilityWordNode(oldNodes[i]);

        generateParentsAndChildren();

        putAllNodesInOneList();
    }
}
