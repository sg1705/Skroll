package com.skroll.pipeline.util;


/**
 * Created by saurabh on 12/23/14.
 */
public class Constants {

    public static final String QUOTE = "\"";

    //training data
    public static final String TRAINING_MODEL_LINE_SEPARATOR = "\n";
    public static final String TRAINING_MODEL_PARA_SEPARATOR = "__;\n";
    public static final String TRAINING_MODEL_TERM_IDENTIFIER = "_pdef";
    public static final String TRAINING_MODEL_TOKEN_SEPARATOR = ",";


    public static final int CATEGORY_POSITIVE = 1;
    public static final int CATEGORY_NEGATIVE = 0;

    public static final boolean DEFINITION_CLASSIFICATION_NAIVE_BAYES_USE_QUOTE=false;

    public static final int DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS_USED = 5;
    public static final int DEFINITION_CLASSIFICATION_NAIVE_BAYES_TOKENS_NUMBER_FEATURE_MAX = 10;
    public static final int DEFINITION_CLASSIFICATION_NAIVE_BAYES_NEGATIVE_THRESHOLD = 10;
    public static final int[] DEFINITION_CLASSIFICATION_NAIVE_BAYES_FEATURE_SIZES =
            {DEFINITION_CLASSIFICATION_NAIVE_BAYES_TOKENS_NUMBER_FEATURE_MAX+1};

    public static final double DEF_THRESHOLD_PROBABILITY = 0.85;
}
