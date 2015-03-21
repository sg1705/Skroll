package com.skroll.analyzer.model;

import com.skroll.analyzer.model.hmm.HiddenMarkovModel;

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



    static final RandomVariableType DEFAULT_PARAGRAPH_CATEGORY = RandomVariableType.PARAGRAPH_HAS_DEFINITION;
    static final RandomVariableType DEFAULT_WORD_TYPE = RandomVariableType.WORD_IS_DEFINED_TERM;

    static final List<RandomVariableType> DEFAULT_DOCUMENT_FEATURES = Arrays.asList(
            RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES
//            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.DOCUMENT_DEFINITIONS_IS_UNDERLINED
    );

    // paragraph features not exist at doc level
    static final List<RandomVariableType> DEFAULT_PARAGRAPH_FEATURES = Arrays.asList(
            RandomVariableType.PARAGRAPH_NUMBER_TOKENS);

//    static final List<RandomVariableType> PARAGRAPH_FEATURES = Arrays.asList(
//            RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
////            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
////            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
////            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.PARAGRAPH_NUMBER_TOKENS);

    static final List<RandomVariableType> DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL = Arrays.asList(
            RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE
//            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
    );
    //todo: if needed, can add a feature to indicated if a word is used as camel case in the document.
    static final List<RandomVariableType> DEFAULT_WORD_FEATURES = Arrays.asList(
            RandomVariableType.WORD_IN_QUOTES
//            RandomVariableType.WORD_HAS_SPECIAL_FORMAT,
            //RandomVariableType.WORD_INDEX
    );

    RandomVariableType paraCategory = DEFAULT_PARAGRAPH_CATEGORY, wordType = DEFAULT_WORD_TYPE;

    List<RandomVariableType> docFeatures,//=DEFAULT_DOCUMENT_FEATURES,
            paraFeatures,//=DEFAULT_PARAGRAPH_FEATURES,
            paraDocFeatures,// =DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL,
            wordFeatures;//=DEFAULT_WORD_FEATURES;

    static List<RandomVariableType> allParagraphFeatures;


    public List<RandomVariableType> getDocFeatures() {
        return docFeatures;
    }

    public List<RandomVariableType> getParaFeatures() {
        return paraFeatures;
    }

    public List<RandomVariableType> getParaDocFeatures() {
        return paraDocFeatures;
    }

    public List<RandomVariableType> getWordFeatures() {
        return wordFeatures;
    }

    public static List<RandomVariableType> getAllParagraphFeatures() {
        return allParagraphFeatures;
    }

    public RandomVariableType getWordType() {
        return wordType;
    }

    public RandomVariableType getParaCategory() {
        return paraCategory;
    }

    public DocumentAnnotatingModel() {


    }

    public HiddenMarkovModel getHmm() {
        return hmm;
    }

    public void initialize(){
        allParagraphFeatures = new ArrayList<>(paraFeatures);
        allParagraphFeatures.addAll(paraDocFeatures);

    }
}

