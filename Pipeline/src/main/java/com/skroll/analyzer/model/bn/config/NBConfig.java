package com.skroll.analyzer.model.bn.config;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.RandomVariableType;

import java.util.List;

/**
 * Created by wei on 4/16/15.
 */
public class NBConfig {
    RandomVariable categoryVar;
    List<RandomVariable> featureVarList;
    List<RandomVariable> wordVarList;

    public NBConfig() {

    }

    public NBConfig(RandomVariable categoryVar,
                    List<RandomVariable> featureVarList, List<RandomVariable> wordVarList) {
        this.categoryVar = categoryVar;
        this.featureVarList = featureVarList;
        this.wordVarList = wordVarList;
    }

    public RandomVariable getCategoryVar() {
        return categoryVar;
    }

    public List<RandomVariable> getFeatureVarList() {
        return featureVarList;
    }

    public List<RandomVariable> getWordVarList() {
        return wordVarList;
    }

}
