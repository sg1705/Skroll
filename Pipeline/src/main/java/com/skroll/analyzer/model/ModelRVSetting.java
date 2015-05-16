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

    public ModelRVSetting(RandomVariable categoryVar,
                          List<RandomVariable> paraFeatureVars,
                          List<RandomVariable> paraDocFeatureVars,
                          List<RandomVariable> docFeatureVars,
                          List<RandomVariable> wordVars) {
        nbfcConfig = new NBFCConfig(categoryVar, paraFeatureVars, paraDocFeatureVars, docFeatureVars, wordVars);
    }

    public NBFCConfig getNbfcConfig() {
        return nbfcConfig;
    }
}
