package com.skroll.analyzer.model;

import com.skroll.analyzer.model.hmm.HiddenMarkovModel;

/**
 * Possible types of random variables and their properties.
 * Each type of random variable may have multiple instances of random variables.
 * For example, the states in HMM are different random variables of the same type with the same distribution,
 * Or, maybe it's better to think all states in HMM are the same random variable,
 * but used multiple times sequentially, just like multiple coin flips of the same coin.
 * Created by wei2learn on 1/24/2015.
 */
public enum RandomVariableType {
    DOCUMENT_DEFINITIONS_NOT_IN_TABLE(2),
    DOCUMENT_DEFINITIONS_IS_UNDERLINED(2),
    DOCUMENT_DEFINITIONS_IN_QUOTES(2),
    DOCUMENT_DEFINITIONS_IS_BOLD(2),
    DOCUMENT_DEFINITIONS_IS_ITALIC(2),
    PARAGRAPH_HAS_DEFINITION(2, new String[]{"no","yes"}),
    PARAGRAPH_HAS_TOC(2, new String[]{"no","yes"}),
    PARAGRAPH_HAS_TOC_1(2, new String[]{"no","yes"}),
    PARAGRAPH_HAS_TOC_2(2, new String[]{"no","yes"}),
    PARAGRAPH_HAS_TOC_3(2, new String[]{"no","yes"}),
    PARAGRAPH_HAS_TOC_4(2, new String[]{"no","yes"}),
    PARAGRAPH_HAS_TOC_5(2, new String[]{"no","yes"}),
    //PARAGRAPH_IS_TOC(2, new String[]{"no","yes"}),
    PARAGRAPH_STARTS_WITH_QUOTE(2),
    PARAGRAPH_STARTS_WITH_BOLD(2),
    PARAGRAPH_STARTS_WITH_UNDERLINE(2),
    PARAGRAPH_STARTS_WITH_ITALIC(2),
    PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT(2),
    PARAGRAPH_NUMBER_TOKENS(10),
    PARAGRAPH_INDEX,
    WORD_IS_TOC_TERM (2),
    WORD_IS_TOC_1_TERM (2),
    WORD_IS_TOC_2_TERM (2),
    WORD_IS_TOC_3_TERM (2),
    WORD_IS_TOC_4_TERM (2),
    WORD_IS_TOC_5_TERM (2),
    WORD_IS_DEFINED_TERM (2),
    WORD_IN_QUOTES (2),
    WORD_IS_BOLD (2),
    WORD_IS_UNDERLINED (2),
    WORD_IS_ITALIC (2),
    WORD_HAS_SPECIAL_FORMAT(2),
    WORD_INDEX(HiddenMarkovModel.DEFAULT_MODEL_LENGTH),

    WORD(0),
    FIRST_WORD(0),
    NEXT_WORD(0),

    // new ones for TOC
    PARAGRAPH_WORDS_STARTS_WITH_UPPERCASE_COUNT (2),
    PARAGRAPH_ALL_WORDS_UPPERCASE (2),
    PARAGRAPH_IS_CENTER_ALIGNED (2),
    PARAGRAPH_HAS_ANCHOR (2),
    PARAGRAPH_NOT_IN_TABLE(2),

    DOCUMENT_TOC_NOT_IN_TABLE(2),
    DOCUMENT_TOC_HAS_ANCHOR(2),
    DOCUMENT_TOC_IS_CENTER_ALIGNED(2),
    DOCUMENT_TOC_HAS_WORDS_UPPERCASE(2),
    DOCUMENT_TOC_IS_UNDERLINED(2),
    DOCUMENT_TOC_IS_ITALIC(2),
    DOCUMENT_TOC_IS_BOLD(2);




    private int featureSize;
    private String[] valueNames;

    RandomVariableType(){
    }
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
