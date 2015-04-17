package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariableType;

import java.util.List;

/**
 * Created by wei on 4/16/15.
 */
public class NBConfig {
    RandomVariableType categoryVar;
    List<RandomVariableType> featureVarList;
    List<RandomVariableType> wordVarList;

    public NBConfig(){

    }

    public NBConfig (RandomVariableType categoryVar,
                     List<RandomVariableType> featureVarList, List<RandomVariableType> wordVarList) {
        this.categoryVar = categoryVar;
        this.featureVarList = featureVarList;
        this.wordVarList = wordVarList;
    }

    public RandomVariableType getCategoryVar() {
        return categoryVar;
    }

    public List<RandomVariableType> getFeatureVarList() {
        return featureVarList;
    }

    public List<RandomVariableType> getWordVarList() {
        return wordVarList;
    }

}
