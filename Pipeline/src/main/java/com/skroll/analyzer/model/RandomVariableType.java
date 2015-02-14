package com.skroll.analyzer.model;

/**
 * Possible types of random variables and their properties.
 * Each type of random variable may have multiple instances of random variables.
 * For example, the states in HMM are different random variables of the same type with the same distribution,
 * Or, maybe it's better to think all states in HMM are the same random variable,
 * but used multiple times sequentially, just like multiple coin flips of the same coin.
 * Created by wei2learn on 1/24/2015.
 */
public enum RandomVariableType {
    PARAGRAPH_HAS_DEFINITION(2, new String[]{"no","yes"}),
    PARAGRAPH_STARTS_WITH_QUOTE(2),
    PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT(2),
    PARAGRAPH_NUMBER_TOKENS(10),
    WORD_IS_DEFINED_TERM (2),
    WORD_IN_QUOTES (2),
    WORD_HAS_SPECIAL_FORMAT(2),
    WORD_INDEX (DefinedTermExtractionModel.HMM_MODEL_LENGTH);

    private int featureSize;
    private String[] valueNames;

    RandomVariableType(int featureSize){
        this.featureSize = featureSize;
    }

    RandomVariableType(int featureSize, String[] valueNames){
        this.featureSize = featureSize;
        this.valueNames = valueNames;
    }

    public int getFeatureSize() {
        return featureSize;
    }

}