package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.bn.node.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 4/12/15.
 */
public class NBInferenceHelper {
    public static NaiveBayes createLogProbabilityNB(NaiveBayes trainingNB){
        DiscreteNode categoryNode = NodeInferenceHelper.createLogProbabilityDiscreteNode( trainingNB.getCategoryNode());

        return new NaiveBayes(
                categoryNode,
                createFeatureNodes(trainingNB.getFeatureNodes(), categoryNode),
                createWordNodes(trainingNB.getWordNodes(), categoryNode)
        );
    }

    static List<DiscreteNode> createFeatureNodes(List<DiscreteNode> trainingFeatureNodes, DiscreteNode categoryNode){
        List<DiscreteNode> featureNodes = new ArrayList<>();
        for (DiscreteNode node: trainingFeatureNodes){
            featureNodes.add( NodeInferenceHelper.createLogProbabilityDiscreteNode(
                    node, Arrays.asList(categoryNode)));
        }
        return featureNodes;
    }

    static List<WordNode> createWordNodes(List<WordNode> trainingWordNodes, DiscreteNode categoryNode){
        List<WordNode> wordNodes = new ArrayList<>();
        for (WordNode node:trainingWordNodes){
            wordNodes.add( NodeInferenceHelper.createLogProbabilityWordNode(node, categoryNode));
        }
        return wordNodes;
    }



    public static NaiveBayesWithFeatureConditions createLogProbNBWithFeatureConditions(
            NaiveBayesWithFeatureConditions tnbf){
        DiscreteNode categoryNode = NodeInferenceHelper.createLogProbabilityDiscreteNode( tnbf.getCategoryNode());
        List<DiscreteNode> docFeatureNodes = createDocFeatureNodes(tnbf.getDocumentFeatureNodes());

        return new NaiveBayesWithFeatureConditions(
                categoryNode,
                createFeatureNodes( tnbf.getFeatureNodes(), categoryNode),
                createFeatureExistAtDocLevelNodes(tnbf.getFeatureExistAtDocLevelNodes(), categoryNode, docFeatureNodes),
                docFeatureNodes,
                createWordNodes(tnbf.getWordNodes(), categoryNode)
        );

    }

    static List<DiscreteNode> createDocFeatureNodes(List<DiscreteNode> trainingDocFeatureNodes){
        List<DiscreteNode> docFeatureNodes =  new ArrayList<>();
        for (DiscreteNode node: trainingDocFeatureNodes){
            docFeatureNodes.add( NodeInferenceHelper.createLogProbabilityDiscreteNode(node));
        }
        return docFeatureNodes;
    }

    static List<List<DiscreteNode>> createNBMNDocFeatureNodes(List<List<DiscreteNode>> trainingDocFeatureNodes) {
        List<List<DiscreteNode>> docFeatureNodes = new ArrayList<>();
        for (List<DiscreteNode> trainingNodesForOneFeature : trainingDocFeatureNodes) {
            List<DiscreteNode> probNodes = new ArrayList<>();
            for (DiscreteNode tNode : trainingNodesForOneFeature) {
                probNodes.add(NodeInferenceHelper.createLogProbabilityDiscreteNode(tNode));
            }
            docFeatureNodes.add(probNodes);
        }
        return docFeatureNodes;
    }

    static List<DiscreteNode> createFeatureExistAtDocLevelNodes(List<DiscreteNode> trainingFeatureExistAtDocLevelNodes,
                                                                DiscreteNode categoryNode,
                                                                List<DiscreteNode> docFeatureNodes){
        List<DiscreteNode> featureExistAtDocLevelNodes = new ArrayList<>();
        for (int i=0; i<trainingFeatureExistAtDocLevelNodes.size();i++){
            featureExistAtDocLevelNodes.add(
                    NodeInferenceHelper.createLogProbabilityDiscreteNode(trainingFeatureExistAtDocLevelNodes.get(i),
                            Arrays.asList(categoryNode, docFeatureNodes.get(i))));
        }
        return featureExistAtDocLevelNodes;
    }


    public static NaiveBayesWithMultiNodes createLogProbNBMN(
            NaiveBayesWithMultiNodes tnbm) {
        DiscreteNode categoryNode = NodeInferenceHelper.createLogProbabilityDiscreteNode(tnbm.getCategoryNode());
        List<List<DiscreteNode>> docFeatureNodes = createNBMNDocFeatureNodes(tnbm.getDocumentFeatureNodes());

        return new NaiveBayesWithMultiNodes(
                categoryNode,
                createFeatureNodes(tnbm.getFeatureNodes(), categoryNode),
                createMultiNodes(tnbm.getMultiNodes(), categoryNode, docFeatureNodes),
                docFeatureNodes,
                createWordNodes(tnbm.getWordNodes(), categoryNode)
        );

    }


    static List<MultiplexNode> createMultiNodes(List<MultiplexNode> trainingFeatureExistAtDocLevelNodes,
                                                DiscreteNode categoryNode,
                                                List<List<DiscreteNode>> docFeatureNodes) {
        List<MultiplexNode> multiplexNodes = new ArrayList<>();
        for (int i = 0; i < trainingFeatureExistAtDocLevelNodes.size(); i++) {
            List<DiscreteNode> parentNodes = new ArrayList<>();
            parentNodes.add(categoryNode);
            parentNodes.addAll(docFeatureNodes.get(i));
            multiplexNodes.add(
                    NodeInferenceHelper.createLogProbabilityMultiplexNode(trainingFeatureExistAtDocLevelNodes.get(i),
                            parentNodes));
        }
        return multiplexNodes;
    }
}
