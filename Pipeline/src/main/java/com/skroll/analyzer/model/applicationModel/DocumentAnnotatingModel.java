package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;

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


    @JsonProperty("modelRVSetting")
    ModelRVSetting modelRVSetting = new DefModelRVSetting();
    @JsonProperty("wordFeatures")
    List<RandomVariable> wordFeatures = modelRVSetting.getWordFeatures();

    @JsonProperty("nbfcConfig")
    NBFCConfig nbfcConfig = modelRVSetting.getNbfcConfig();
    @JsonProperty("wordType")
    RandomVariable wordType = modelRVSetting.getWordType();


    // @JsonIgnore
    public RandomVariable getParaCategory() {
        return nbfcConfig.getCategoryVar();
    }

    public DocumentAnnotatingModel() {
    }

    public HiddenMarkovModel getHmm() {
        return hmm;
    }

    public NBFCConfig getNbfcConfig() {
        return nbfcConfig;
    }
}

