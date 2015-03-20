package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.NaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * todo: for training, can pass in a doc object instead of store it
 * todo: for using the model, after initializing in constructor, can store just the features exist at doc level for each paragraph
 * Created by wei2learn on 2/16/2015.
 */
public abstract class DocumentAnnotatingModel {
    static final int HMM_MODEL_LENGTH = 12;

    HiddenMarkovModel hmm;



    static final List<RandomVariableType> DOCUMENT_FEATURES = Arrays.asList(
            RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES
//            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.DOCUMENT_DEFINITIONS_IS_UNDERLINED
    );

    // paragraph features not exist at doc level
    static final List<RandomVariableType> PARAGRAPH_FEATURES = Arrays.asList(
            RandomVariableType.PARAGRAPH_NUMBER_TOKENS);

//    static final List<RandomVariableType> PARAGRAPH_FEATURES = Arrays.asList(
//            RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
////            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
////            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
////            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.PARAGRAPH_NUMBER_TOKENS);

    static final List<RandomVariableType> PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL = Arrays.asList(
            RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE
//            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
    );

    static List<RandomVariableType> allParagraphFeatures;




    //todo: if needed, can add a feature to indicated if a word is used as camel case in the document.
    static final List<RandomVariableType> WORD_FEATURES = Arrays.asList(
            RandomVariableType.WORD_IN_QUOTES
//            RandomVariableType.WORD_HAS_SPECIAL_FORMAT,
            //RandomVariableType.WORD_INDEX
    );



    public DocumentAnnotatingModel() {
        allParagraphFeatures = new ArrayList<>(PARAGRAPH_FEATURES);
        allParagraphFeatures.addAll(PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL);

    }

    public HiddenMarkovModel getHmm() {
        return hmm;
    }
}

