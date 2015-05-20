package com.skroll.analyzer.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
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
//
//
////    public static final RandomVariable DEFAULT_PARAGRAPH_CATEGORY = RandomVariable.PARAGRAPH_HAS_DEFINITION;
//    public static final RandomVariable DEFAULT_WORD_TYPE = RandomVariable.WORD_IS_DEFINED_TERM;
//
//    public static final List<RandomVariable> DEFAULT_WORDS = Arrays.asList(
//            RandomVariable.WORD
//    );
//
//
//    public static final List<RandomVariable> DEFAULT_DOCUMENT_FEATURES = Arrays.asList(
//            RandomVariable.DOCUMENT_DEFINITIONS_NOT_IN_TABLE,
//            RandomVariable.DOCUMENT_DEFINITIONS_IS_ITALIC,
//            RandomVariable.DOCUMENT_DEFINITIONS_IS_UNDERLINED,
//            RandomVariable.DOCUMENT_DEFINITIONS_IS_BOLD
//
//            ,            RandomVariable.DOCUMENT_DEFINITIONS_IN_QUOTES
//
//    );
//
//    // paragraph features not exist at doc level
//    public static final List<RandomVariable> DEFAULT_PARAGRAPH_FEATURES = Arrays.asList(
//            RandomVariable.PARAGRAPH_NUMBER_TOKENS);
//
//    public static final List<RandomVariable> DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL = Arrays.asList(
//            RandomVariable.PARAGRAPH_NOT_IN_TABLE,
//            RandomVariable.PARAGRAPH_STARTS_WITH_ITALIC,
//            RandomVariable.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariable.PARAGRAPH_STARTS_WITH_BOLD,
//
//            RandomVariable.PARAGRAPH_STARTS_WITH_QUOTE
//
//    );
//    //todo: if needed, can add a feature to indicated if a word is used as camel case in the document.
//    public static final List<RandomVariable> DEFAULT_WORD_FEATURES = Arrays.asList(
//            RandomVariable.WORD_IN_QUOTES,
//            RandomVariable.WORD_IS_UNDERLINED
//            //RandomVariable.WORD_INDEX
//    );
//
//    public static final NBFCConfig DEFAULT_NBFC_CONFIG = new DefModelRVSetting().getNbfcConfig();
////    public static final NBFCConfig DEFAULT_NBFC_CONFIG = new NBFCConfig(
////            DEFAULT_PARAGRAPH_CATEGORY, DEFAULT_PARAGRAPH_FEATURES,
////            DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL, DEFAULT_DOCUMENT_FEATURES, DEFAULT_WORDS);
//
//    public RandomVariable wordType = DEFAULT_WORD_TYPE;


//    List<RandomVariable> docFeatures,
//            paraFeatures,
//            paraDocFeatures,
//            wordFeatures,
//    wordVarList;

//    List<RandomVariable> allParagraphFeatures;

    ModelRVSetting modelRVSetting = new DefModelRVSetting();
    List<RandomVariable> wordFeatures = modelRVSetting.getWordFeatures();
    NBFCConfig nbfcConfig = modelRVSetting.getNbfcConfig();
    RandomVariable wordType = modelRVSetting.getWordType();

//    @JsonIgnore
//    public List<RandomVariable> getDocFeatures() {
//        return docFeatures;
//    }
//
//    @JsonIgnore
//    public List<RandomVariable> getParaFeatures() {
//        return paraFeatures;
//    }
//
//    @JsonIgnore
//    public List<RandomVariable> getParaDocFeatures() {
//        return paraDocFeatures;
//    }
//
//    @JsonIgnore
//    public List<RandomVariable> getWordFeatures() {
//        return wordFeatures;
//    }
//
//    @JsonIgnore
//    public List<RandomVariable> getAllParagraphFeatures() {
//        return allParagraphFeatures;
//    }


    //    @JsonIgnore
    public RandomVariable getParaCategory() {
        return nbfcConfig.getCategoryVar();
    }

    public DocumentAnnotatingModel() {


    }

    public HiddenMarkovModel getHmm() {
        return hmm;
    }

//    void initialize(){
//        allParagraphFeatures = new ArrayList<>(nbfcConfig.getFeatureVarList());
//        allParagraphFeatures.addAll(nbfcConfig.getFeatureExistsAtDocLevelVarList());
//    }

    public NBFCConfig getNbfcConfig() {
        return nbfcConfig;
    }
}

