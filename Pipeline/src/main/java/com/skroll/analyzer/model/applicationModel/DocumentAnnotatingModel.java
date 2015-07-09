package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.NaiveBayesWithMultiNodes;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.classifier.Category;

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
    ModelRVSetting modelRVSetting = new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME,2);
    @JsonProperty("wordFeatures")
    List<RandomVariable> wordFeatures = modelRVSetting.getWordFeatures();

    @JsonProperty("nbmnConfig")
    NBMNConfig nbmnConfig = modelRVSetting.getNbmnConfig();
    @JsonProperty("wordType")
    RandomVariable wordType = modelRVSetting.getWordType();


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


}

