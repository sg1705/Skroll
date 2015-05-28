package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.analyzer.model.bn.config.NBFCConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * need to link RVs with it's sources
 * need to group RVs by its usages
 * Created by wei on 5/11/15.
 */
public class ModelRVSetting {

    public static final int NUM_WORDS_TO_USE_PER_PARAGRAPH = 10;
    @JsonProperty("nbfcConfig")
    NBFCConfig nbfcConfig;
    @JsonProperty("wordType")
    RandomVariable wordType;
    @JsonProperty("wordFeatures")
    List<RandomVariable> wordFeatures;

    /**
     * Defines the model
     *
     * @param wordType category of the word. For example, variable to indicate if a word is a defined term.
     *                 Used at HMM level.
     * @param wordFeatures features for each. For example, if the word is in quota. Used at HMM level.
     * @param paraType category for the paragraph.
     * @param paraFeatureVars features for the paragraph
     * @param paraDocFeatureVars paragraph that are considered at doc level
     * @param wordVars type of words. For example - first words, unique words etc.
     */
    public ModelRVSetting(RandomVariable wordType,
                          List<RandomVariable> wordFeatures,
                          RandomVariable paraType,
                          List<RandomVariable> paraFeatureVars,
                          List<RandomVariable> paraDocFeatureVars,
                          List<RandomVariable> wordVars) {
        nbfcConfig = new NBFCConfig(paraType, paraFeatureVars, paraDocFeatureVars,
                RVCreater.createDocFeatureRVs(paraDocFeatureVars), wordVars);
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
    }


    public ModelRVSetting(
            @JsonProperty("nbfcConfig")NBFCConfig nbfcConfig,
            @JsonProperty("wordType")RandomVariable wordType,
            @JsonProperty("wordFeatures")List<RandomVariable> wordFeatures) {
        this.nbfcConfig = nbfcConfig;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
    }



    public NBFCConfig getNbfcConfig() {
        return nbfcConfig;
    }

    public RandomVariable getWordType() {
        return wordType;
    }

    public List<RandomVariable> getWordFeatures() {
        return wordFeatures;
    }
}
