package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 4/12/15.
 */
public class NBTrainingHelper {
    public static NaiveBayes createTrainingNB(RandomVariableType categoryVar,
                                   List<RandomVariableType> featureVarList, List<RandomVariableType> wordVarList){

        DiscreteNode categoryNode = NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(categoryVar));

        return new NaiveBayes(
                categoryNode,
                createFeatureNodes(featureVarList, categoryVar, categoryNode),
                createWordNodes(wordVarList, categoryNode)
        );

    }

    static List<DiscreteNode> createFeatureNodes(List<RandomVariableType> featureVarList,
                                                 RandomVariableType categoryVar, DiscreteNode categoryNode){
        List<DiscreteNode> featureNodes = new ArrayList<>();
        for (RandomVariableType featureVar: featureVarList){
            featureNodes.add( NodeTrainingHelper.createTrainingDiscreteNode( Arrays.asList( featureVar, categoryVar),
                    Arrays.asList(categoryNode)));
        }
        return featureNodes;
    }

    static List<WordNode> createWordNodes(List<RandomVariableType> wordVarList, DiscreteNode categoryNode){
        List<WordNode> wordNodes = new ArrayList<>();
        for (RandomVariableType var: wordVarList){
            wordNodes.add( NodeTrainingHelper.createTrainingWordNode(categoryNode));
        }
        return wordNodes;
    }

    public static void addSample(NaiveBayes nb, SimpleDataTuple tuple){
        addSample(nb, tuple, 1.0);
    }

    public static void addSample(NaiveBayes bn, SimpleDataTuple tuple, double weight){
        bn.setObservation(tuple);
        for (DiscreteNode node: bn.getAllDiscreteNodes()){
             NodeTrainingHelper.updateCount(node, weight);
        }

        for (WordNode node: bn.getWordNodes()){
            NodeTrainingHelper.updateCount(node, weight);
        }
        bn.clearObservation(); // probably unnecessary
    }



    public static NaiveBayesWithFeatureConditions createTrainingNBWithFeatureConditioning(
            RandomVariableType categoryVar,
            List<RandomVariableType> featureVarList,
            List<RandomVariableType> featureExistsAtDocLevelVarList,
            List<RandomVariableType> documentFeatureVarList,
            List<RandomVariableType> wordVarList ) {

        DiscreteNode categoryNode = NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(categoryVar));
        List<DiscreteNode> docFeatureNodes = createDocFeatureNodes(documentFeatureVarList);

        return new NaiveBayesWithFeatureConditions(
                categoryNode,
                createFeatureNodes(featureVarList, categoryVar, categoryNode),
                createFeatureExistAtDoclevelNodes(featureExistsAtDocLevelVarList, docFeatureNodes, categoryNode),
                docFeatureNodes,
                createWordNodes(wordVarList, categoryNode)
        );
    }

    static List<DiscreteNode> createDocFeatureNodes(List<RandomVariableType> docFeatureVarList){
        List<DiscreteNode> docFeatureNodes= new ArrayList<>();
        for (RandomVariableType var: docFeatureVarList){
            docFeatureNodes.add( NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(var)));
        }
        return docFeatureNodes;
    }

    static List<DiscreteNode> createFeatureExistAtDoclevelNodes(
            List<RandomVariableType> featureExistsAtDocLevelVarList,
            List<DiscreteNode>  docFeatureNodes,
            DiscreteNode categoryNode){

        List<DiscreteNode> nodes= new ArrayList<>();
        for (int i=0; i<featureExistsAtDocLevelVarList.size(); i++){
            nodes.add( NodeTrainingHelper.createTrainingDiscreteNode(
                    Arrays.asList(featureExistsAtDocLevelVarList.get(i), categoryNode.getVariable(),
                            docFeatureNodes.get(i).getVariable()),
                    Arrays.asList(categoryNode, docFeatureNodes.get(i))));
        }
        return nodes;

    }

}
