package com.skroll.analyzer.model;

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
    NBFCConfig nbfcConfig;
    RandomVariable wordType;
    List<RandomVariable> wordFeatures;

    public ModelRVSetting(RandomVariable wordType,
                          List<RandomVariable> wordFeatures,
                          RandomVariable paraType,
                          List<RandomVariable> paraFeatureVars,
                          List<RandomVariable> paraDocFeatureVars,
                          List<RandomVariable> docFeatureVars,
                          List<RandomVariable> wordVars) {
        nbfcConfig = new NBFCConfig(paraType, paraFeatureVars, paraDocFeatureVars, docFeatureVars, wordVars);
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
