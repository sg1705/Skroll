package com.skroll.analyzer.model.bn.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.RandomVariableType;

import java.util.List;

/**
 * Created by wei on 4/16/15.
 */
public class NBConfig {

    @JsonProperty("categoryVar")
    RandomVariable categoryVar;
    @JsonProperty("featureVarList")
    List<RandomVariable> featureVarList;
    @JsonProperty("wordVarList")
    List<RandomVariable> wordVarList;

    public NBConfig() {

    }

    /**
     *
     * @param categoryVar categoryType
     * @param featureVarList features of a given category
     * @param wordVarList type of words. For example - all the words in a paragraph, or first word,
     *                    or other set of words.
     */
    public NBConfig(@JsonProperty("categoryVar")RandomVariable categoryVar,
                    @JsonProperty("featureVarList")List<RandomVariable> featureVarList,
                    @JsonProperty("wordVarList")List<RandomVariable> wordVarList) {
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
