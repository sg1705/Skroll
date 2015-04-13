package com.skroll.analyzer.model.bn;

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

}
