//package com.skroll.analyzer.model.bn;
//
//import com.skroll.analyzer.model.bn.node.*;
//
///**
// * Created by wei2learn on 3/11/2015.
// */
//public class LogProbabilityNaiveBayesWithFeatureConditions extends NaiveBayesWithFeatureConditions {
//
//    public LogProbabilityNaiveBayesWithFeatureConditions(TrainingNaiveBayesWithFeatureConditions tnb) {
//        categoryNode = new LogProbabilityDiscreteNode((TrainingDiscreteNode) tnb.getCategoryNode());
//        TrainingDiscreteNode[] tdfNodeArray = (TrainingDiscreteNode[]) tnb.getDocumentFeatureNodeArray();
//        TrainingDiscreteNode[] tfNodeArray = (TrainingDiscreteNode[]) tnb.getFeatureNodeArray();
//        TrainingDiscreteNode[] tfedNodeArray = (TrainingDiscreteNode[]) tnb.getFeatureExistAtDocLevelArray();
//
//        featureNodeArray = new LogProbabilityDiscreteNode[ tfNodeArray.length];
//        documentFeatureNodeArray = new LogProbabilityDiscreteNode[ tdfNodeArray.length];
//        featureExistAtDocLevelArray = new LogProbabilityDiscreteNode[ tfedNodeArray.length];
//        for (int i=0; i<featureNodeArray.length; i++) {
//            featureNodeArray[i] = new LogProbabilityDiscreteNode(tfNodeArray[i]);
//        }
//        for (int i=0; i<documentFeatureNodeArray.length;i++)
//            documentFeatureNodeArray[i] = new LogProbabilityDiscreteNode( tdfNodeArray[i]);
//        for (int i=0; i<featureExistAtDocLevelArray.length;i++)
//            featureExistAtDocLevelArray[i] = new LogProbabilityDiscreteNode( tfedNodeArray[i]);
//
//
//        TrainingWordNode[] trainingWordNodes = (TrainingWordNode[]) tnb.getWordNodeArray();
//        wordNodeArray = new LogProbabilityWordNode[trainingWordNodes.length];
//        for (int i=0; i<trainingWordNodes.length; i++)
//            wordNodeArray[i] = new LogProbabilityWordNode(trainingWordNodes[i]);
//
//        generateParentsAndChildren();
//        putAllNodesInOneList();
//    }
//
//    /**
//     * copy constructor
//     */
//    public LogProbabilityNaiveBayesWithFeatureConditions(LogProbabilityNaiveBayesWithFeatureConditions pnb) {
//        categoryNode = new LogProbabilityDiscreteNode((LogProbabilityDiscreteNode) pnb.getCategoryNode());
//        LogProbabilityDiscreteNode[] pdfNodeArray = (LogProbabilityDiscreteNode[]) pnb.getDocumentFeatureNodeArray();
//        LogProbabilityDiscreteNode[] pfNodeArray = (LogProbabilityDiscreteNode[]) pnb.getFeatureNodeArray();
//        LogProbabilityDiscreteNode[] pfedNodeArray = (LogProbabilityDiscreteNode[]) pnb.getFeatureExistAtDocLevelArray();
//
//        documentFeatureNodeArray = new LogProbabilityDiscreteNode[ pdfNodeArray.length];
//        featureNodeArray = new LogProbabilityDiscreteNode[ pfNodeArray.length];
//        featureExistAtDocLevelArray = new LogProbabilityDiscreteNode[ pfedNodeArray.length];
//
//        for (int i=0; i<featureNodeArray.length; i++) {
//            featureNodeArray[i] = new LogProbabilityDiscreteNode(pfNodeArray[i]);
//        }
//        for (int i=0; i<documentFeatureNodeArray.length;i++)
//            documentFeatureNodeArray[i] = new LogProbabilityDiscreteNode( pdfNodeArray[i]);
//        for (int i=0; i<featureExistAtDocLevelArray.length;i++)
//            featureExistAtDocLevelArray[i] = new LogProbabilityDiscreteNode( pfedNodeArray[i]);
//
//        LogProbabilityWordNode[] oldNodes = (LogProbabilityWordNode[]) pnb.getWordNodeArray();
//        wordNodeArray = new LogProbabilityWordNode[oldNodes.length];
//        for (int i=0; i<oldNodes.length; i++)
//            wordNodeArray[i] = new LogProbabilityWordNode(oldNodes[i]);
//
//        generateParentsAndChildren();
//
//        putAllNodesInOneList();
//    }
//}
