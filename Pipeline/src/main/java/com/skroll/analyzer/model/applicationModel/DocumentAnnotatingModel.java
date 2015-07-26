package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.bn.node.MultiplexNode;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.util.Visualizer;

import java.util.HashMap;
import java.util.List;

/**
 * todo: for training, can pass in a doc object instead of store it
 * todo: for using the model, after initializing in constructor, can store just the features exist at doc level for each paragraph
 * Created by wei2learn on 2/16/2015.
 */
public abstract class DocumentAnnotatingModel {
    static final int HMM_MODEL_LENGTH = 12;

    @JsonProperty("hmm")
    HiddenMarkovModel hmm;

    @JsonProperty("nbmnModel")
    NaiveBayesWithMultiNodes nbmnModel;

    @JsonProperty("modelRVSetting")
    ModelRVSetting modelRVSetting;

    @JsonProperty("wordFeatures")
    List<RandomVariable> wordFeatures;

    @JsonProperty("nbmnConfig")
    NBMNConfig nbmnConfig;

    @JsonProperty("wordType")
    RandomVariable wordType;


    @JsonProperty("id")
    int id;

    @JsonIgnore
    public RandomVariable getParaCategory() {
        return nbmnConfig.getCategoryVar();
    }

    public DocumentAnnotatingModel() {
    }

    @JsonIgnore
    public HiddenMarkovModel getHmm() {
        return hmm;
    }

    @JsonIgnore
    public NBMNConfig getNbmnConfig() {
        return nbmnConfig;
    }

    @JsonIgnore
    public NaiveBayesWithMultiNodes getNbmnModel() {
        return nbmnModel;
    }

    public HashMap<String, HashMap<String, HashMap<String, Double>>> toVisualMap(
            HashMap<String, HashMap<String, HashMap<String, Double>>> map) {

        //document level features
        List<DiscreteNode> discreteNodes = this.nbmnModel.getAllDiscreteNodes();
        map.put("discreteNodes", Visualizer.nodesToMap(
                discreteNodes.toArray(new DiscreteNode[discreteNodes.size()])));

        List<MultiplexNode> multiplexNodes = getNbmnModel().getMultiNodes();
        for (int i = 0; i < multiplexNodes.size(); i++) {
            DiscreteNode[] nodes = multiplexNodes.get(i).getNodes();
            for (int j = 0; j < nodes.length; j++) {
                DiscreteNode node = nodes[j];
                map.put("F" + i + "C" + j + node.getVariable().getName(), Visualizer.nodesToMap(
                        new DiscreteNode[]{node.getParents()[0], node}));
            }

        }

        return map;
    }

}

