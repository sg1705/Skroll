package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.config.NBConfig;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
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
                    Arrays.asList( featureVar, config.getCategoryVar()), Arrays.asList(categoryNode)));
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

}
