package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.config.NBConfig;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.bn.node.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 4/12/15.
 */
public class NBTrainingHelper {
    public static NaiveBayes createTrainingNB(NBConfig config){

        DiscreteNode categoryNode = NodeTrainingHelper.createTrainingDiscreteNode(
                Arrays.asList(config.getCategoryVar()));

        return new NaiveBayes(
                categoryNode,
                createFeatureNodes(config, categoryNode),
                createWordNodes(config, categoryNode)
        );

    }

    static List<DiscreteNode> createFeatureNodes(NBConfig config, DiscreteNode categoryNode){
        List<DiscreteNode> featureNodes = new ArrayList<>();
        for (RandomVariable featureVar : config.getFeatureVarList()) {
            featureNodes.add( NodeTrainingHelper.createTrainingDiscreteNode(
                    Arrays.asList(featureVar, config.getCategoryVar()), Arrays.asList(categoryNode)));
        }
        return featureNodes;
    }

    static List<WordNode> createWordNodes(NBConfig config, DiscreteNode categoryNode){
        List<WordNode> wordNodes = new ArrayList<>();
        for (RandomVariable var : config.getWordVarList()) {
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

    public static void addSample(NaiveBayesWithMultiNodes nb, NBMNTuple tuple) {
        addSample(nb, tuple, 1.0);
    }

    public static void addSample(NaiveBayesWithMultiNodes bn, NBMNTuple tuple, double weight) {
        bn.setObservation(tuple);
        for (DiscreteNode node : bn.getAllDiscreteNodes()) {
            NodeTrainingHelper.updateCount(node, weight);
        }

        for (MultiplexNode node : bn.getMultiNodes()) {
            DiscreteNode activeNode = node.getActiveNode();
            DiscreteNode activeParent = node.getActiveNode().getParents()[0];

            // skip update if either the node or the corresponding doc feature is unobserved
            // cannot have the case where node is observed but corresponding doc feature is not observed,
            // since any observed node would also set the correspondign doc feature.
            if (activeNode.getObservation() == -1 || activeParent.getObservation() == -1) continue;

            NodeTrainingHelper.updateCount(activeNode, weight);
            NodeTrainingHelper.updateCount(activeParent, weight);
        }

        for (WordNode node : bn.getWordNodes()) {
            // update the count normalized by the length
            NodeTrainingHelper.updateCount(node, weight/node.getObservation().length);
        }
        bn.clearObservation(); // probably unnecessary
    }

    public static NaiveBayesWithFeatureConditions createTrainingNBWithFeatureConditioning(NBFCConfig config) {

        DiscreteNode categoryNode = NodeTrainingHelper.createTrainingDiscreteNode(
                Arrays.asList(config.getCategoryVar()));
        List<DiscreteNode> docFeatureNodes = createDocFeatureNodes(config);

        return new NaiveBayesWithFeatureConditions(
                categoryNode,
                createFeatureNodes(config, categoryNode),
                createFeatureExistAtDoclevelNodes(config, docFeatureNodes, categoryNode),
                docFeatureNodes,
                createWordNodes(config, categoryNode)
        );
    }

    static List<DiscreteNode> createDocFeatureNodes(NBFCConfig config){
        List<DiscreteNode> docFeatureNodes= new ArrayList<>();
        for (RandomVariable var : config.getDocumentFeatureVarList()) {
            docFeatureNodes.add( NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(var)));
        }
        return docFeatureNodes;
    }

    static List<List<DiscreteNode>> createNBMNDocFeatureNodes(NBMNConfig config) {
        List<List<DiscreteNode>> docFeatureNodes = new ArrayList<>();
        for (List<RandomVariable> rvs : config.getDocumentFeatureVarList()) {
            List<DiscreteNode> nodes = new ArrayList<>();
            for (RandomVariable rv : rvs) {
                nodes.add(NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(rv)));
            }
            docFeatureNodes.add(nodes);
        }
        return docFeatureNodes;
    }

    static List<DiscreteNode> createFeatureExistAtDoclevelNodes(
            NBFCConfig config, List<DiscreteNode>  docFeatureNodes, DiscreteNode categoryNode){

        List<RandomVariable> featureExistsAtDocLevelVarList = config.getFeatureExistsAtDocLevelVarList();
        List<DiscreteNode> nodes= new ArrayList<>();
        for (int i=0; i<featureExistsAtDocLevelVarList.size(); i++){
            nodes.add( NodeTrainingHelper.createTrainingDiscreteNode(
                    Arrays.asList(featureExistsAtDocLevelVarList.get(i), categoryNode.getVariable(),
                            docFeatureNodes.get(i).getVariable()),
                    Arrays.asList(categoryNode, docFeatureNodes.get(i))));
        }
        return nodes;

    }

    public static NaiveBayesWithMultiNodes createTrainingNBMN(NBMNConfig config) {

        DiscreteNode categoryNode = NodeTrainingHelper.createTrainingDiscreteNode(
                Arrays.asList(config.getCategoryVar()));
        List<List<DiscreteNode>> docFeatureNodes = createNBMNDocFeatureNodes(config);

        return new NaiveBayesWithMultiNodes(
                categoryNode,
                createFeatureNodes(config, categoryNode),
                createMultiNodes(config, docFeatureNodes, categoryNode),
                docFeatureNodes,
                createWordNodes(config, categoryNode)
        );
    }

    static List<MultiplexNode> createMultiNodes(
            NBMNConfig config, List<List<DiscreteNode>> docFeatureNodes, DiscreteNode categoryNode) {

        List<RandomVariable> featureExistsAtDocLevelVarList = config.getFeatureExistsAtDocLevelVarList();
        List<MultiplexNode> nodes = new ArrayList<>();
        for (int i = 0; i < featureExistsAtDocLevelVarList.size(); i++) {
            List<RandomVariable> familyVars = new ArrayList<>();
            familyVars.add(featureExistsAtDocLevelVarList.get(i));
            familyVars.add(categoryNode.getVariable());
            familyVars.addAll(config.getDocumentFeatureVarList().get(i));

            List<DiscreteNode> parentNodes = new ArrayList<>();
            parentNodes.add(categoryNode);
//            parentNodes.addAll(docFeatureNodes.get(i));

            nodes.add(NodeTrainingHelper.createTrainingMultiplexNode(familyVars, categoryNode, docFeatureNodes.get(i)));
        }
        return nodes;

    }

}
