package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.NaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.ProbabilityNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 2/16/2015.
 */
public class DocumentAnnotatingModel {
    static final int HMM_MODEL_LENGTH = 12;

    NaiveBayesWithFeatureConditions nbfModel;
    HiddenMarkovModel hmm;

    static final List<RandomVariableType> DOCUMENT_FEATURES = Arrays.asList(
            RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES
//            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.DOCUMENT_DEFINITIONS_IS_UNDERLINED
    );
    static final List<RandomVariableType> PARAGRAPH_FEATURES = Arrays.asList(
            RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_SPECIAL_FORMAT,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
//            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,

            RandomVariableType.PARAGRAPH_NUMBER_TOKENS);

    //todo: if needed, can add a feature to indicated if a word is used as camel case in the document.
    static final List<RandomVariableType> WORD_FEATURES = Arrays.asList(
            RandomVariableType.WORD_IN_QUOTES
//            RandomVariableType.WORD_HAS_SPECIAL_FORMAT,
            //RandomVariableType.WORD_INDEX
    );

    public DocumentAnnotatingModel() {


    }

    public DocumentAnnotatingModel(TrainingNaiveBayesWithFeatureConditions tnbfModel) {

        this.nbfModel = tnbfModel;

        int[] wordFeatureSizes = new int[WORD_FEATURES.size()]; // include state at the feature index 0.
        for (int i = 0; i < wordFeatureSizes.length; i++)
            wordFeatureSizes[i] = WORD_FEATURES.get(i).getFeatureSize();
        hmm = new HiddenMarkovModel(HMM_MODEL_LENGTH,
                RandomVariableType.WORD_IS_DEFINED_TERM.getFeatureSize(), wordFeatureSizes);
    }

}

