//package com.skroll.analyzer.model.bn;
//
//import com.fasterxml.jackson.annotation.JsonCreator;
//import com.fasterxml.jackson.annotation.JsonProperty;
//import com.skroll.analyzer.model.RandomVariableType;
//import com.skroll.analyzer.model.bn.node.TrainingDiscreteNode;
//import com.skroll.analyzer.model.bn.node.TrainingWordNode;
//
//import java.util.*;
//
///**
// * Created by wei2learn on 1/3/2015.
// */
//public class TrainingNaiveBayesWithFeatureConditions extends NaiveBayesWithFeatureConditions implements TrainingBN{
//
//
//    //constructor added to make json work
//    @JsonCreator
//    public TrainingNaiveBayesWithFeatureConditions(
//            @JsonProperty("wordNode")TrainingWordNode[] wordNodeArray,
//            @JsonProperty("documentFeatureNodeArray")TrainingDiscreteNode[]  documentFeatureNodeArray,
//            @JsonProperty("featureExistAtDocLevelArray")TrainingDiscreteNode[] featureExistAtDocLevelArray,
//            @JsonProperty("categoryNode")TrainingDiscreteNode categoryNode,
//            @JsonProperty("featureNodeArray") TrainingDiscreteNode[] featureNodeArray,
//            @JsonProperty("discreteNodeArray")TrainingDiscreteNode[] discreteNodeArray) {
//
//        this.wordNodeArray = wordNodeArray;
//        this.documentFeatureNodeArray = documentFeatureNodeArray;
//        this.featureExistAtDocLevelArray = featureExistAtDocLevelArray;
//        this.categoryNode = categoryNode;
//        this.featureNodeArray = featureNodeArray;
//        this.discreteNodeArray = discreteNodeArray;
//    }
//
//
//    /**
//     * copy constructor
//     */
//    public TrainingNaiveBayesWithFeatureConditions(TrainingNaiveBayesWithFeatureConditions tnbf){
//        categoryNode = new TrainingDiscreteNode((TrainingDiscreteNode) tnbf.getCategoryNode());
//        TrainingDiscreteNode[] pdfNodeArray = (TrainingDiscreteNode[]) tnbf.getDocumentFeatureNodeArray();
//        TrainingDiscreteNode[] pfNodeArray = (TrainingDiscreteNode[]) tnbf.getFeatureNodeArray();
//        TrainingDiscreteNode[] pfedNodeArray = (TrainingDiscreteNode[]) tnbf.getFeatureExistAtDocLevelArray();
//
//        documentFeatureNodeArray = new TrainingDiscreteNode[ pdfNodeArray.length];
//        featureNodeArray = new TrainingDiscreteNode[ pfNodeArray.length];
//        featureExistAtDocLevelArray = new TrainingDiscreteNode[ pfedNodeArray.length];
//
//        for (int i=0; i<featureNodeArray.length; i++) {
//            featureNodeArray[i] = new TrainingDiscreteNode(pfNodeArray[i]);
//        }
//        for (int i=0; i<documentFeatureNodeArray.length;i++)
//            documentFeatureNodeArray[i] = new TrainingDiscreteNode( pdfNodeArray[i]);
//        for (int i=0; i<featureExistAtDocLevelArray.length;i++)
//            featureExistAtDocLevelArray[i] = new TrainingDiscreteNode( pfedNodeArray[i]);
//
//        TrainingWordNode[] oldWordNodeArray = (TrainingWordNode[]) tnbf.getWordNodeArray();
//        TrainingWordNode[] newWordNodeArray = new TrainingWordNode[oldWordNodeArray.length];
//        for (int i=0; i<oldWordNodeArray.length;i++){
//            newWordNodeArray[i] = new TrainingWordNode(oldWordNodeArray[i]);
//        }
//        generateParentsAndChildren();
//
//        putAllNodesInOneList();
//
//
//
//    }
//
//
//    // assuming the documentFeatures match the front sublist of the features list
//
//    public TrainingNaiveBayesWithFeatureConditions(RandomVariableType categoryVar,
//                                                   List<RandomVariableType> featureVarList,
//                                                   List<RandomVariableType> featureExistsAtDocLevelVarList,
//                                                   List<RandomVariableType> documentFeatureVarList,
//                                                   List<RandomVariableType> wordVarList ) {
//
//        categoryNode = new TrainingDiscreteNode(Arrays.asList(categoryVar));
//        featureNodeArray = new TrainingDiscreteNode[ featureVarList.size()];
//        documentFeatureNodeArray = new TrainingDiscreteNode[ documentFeatureVarList.size()];
//        featureExistAtDocLevelArray = new TrainingDiscreteNode[ featureExistsAtDocLevelVarList.size()];
//
//        for (int i=0; i<documentFeatureVarList.size(); i++) {
//            documentFeatureNodeArray[i]=new TrainingDiscreteNode( Arrays.asList( documentFeatureVarList.get(i) ));
//            featureExistAtDocLevelArray[i] = new TrainingDiscreteNode(
//                    Arrays.asList(featureExistsAtDocLevelVarList.get(i), categoryVar, documentFeatureVarList.get(i)));
//        }
//
//        for (int i=0; i<featureVarList.size(); i++) {
//            featureNodeArray[i] = new TrainingDiscreteNode(
//                    Arrays.asList(featureVarList.get(i), categoryVar));
//        }
//
//        wordNodeArray = new TrainingWordNode[wordVarList.size()];
//        for (int i=0; i<wordVarList.size(); i++){
//            wordNodeArray[i] =  new TrainingWordNode((TrainingDiscreteNode)categoryNode);
//        }
//
//        generateParentsAndChildren();
//
//        putAllNodesInOneList();
//
//
//    }
//
//
//    public void addSample(SimpleDataTuple tuple){
//        BNHelper.addSample(this, tuple);
//    }
//
//    public void addSample(SimpleDataTuple tuple, double weight){
//        BNHelper.addSample(this, tuple, weight);
//
//    }
//
//
//
//}
