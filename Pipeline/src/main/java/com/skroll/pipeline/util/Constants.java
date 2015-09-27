package com.skroll.pipeline.util;


import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

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

    public static final boolean DEFINITION_CLASSIFICATION_NAIVE_BAYES_USE_QUOTE=true;
    public static final boolean DEFINITION_CLASSIFICATION_HMM_USE_QUOTE=true;


    public static final int DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS_USED = 10;
    public static final int DEFINITION_CLASSIFICATION_NAIVE_BAYES_TOKENS_NUMBER_FEATURE_MAX = 11;
    public static final int DEFINITION_CLASSIFICATION_NAIVE_BAYES_NEGATIVE_THRESHOLD = 0;
    public static final int[] DEFINITION_CLASSIFICATION_NAIVE_BAYES_FEATURE_SIZES =
            {2, DEFINITION_CLASSIFICATION_NAIVE_BAYES_TOKENS_NUMBER_FEATURE_MAX+1};

    public static final double DEF_THRESHOLD_PROBABILITY = 0.85;


    // extractor url
    public static final String PHANTOM_JS_BIN = "phantom_js_bin";
    public static final String PHANTOM_JS_BIN_WINDOWS = "phantom_js_bin_windows";
    public static final String PHANTOM_JS_BIN_MAC = "phantom_js_bin_mac";
    public static final String JQUERY_PARSER_JS = "jquery_parser_js";

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    // Logging markers
    public static final String LINKER_MARKER = "LINK_MARKER";


    public static Set<String> SPECIAL_STOP_WORDS = new HashSet<String>();

    static {
        SPECIAL_STOP_WORDS.add(">");
        SPECIAL_STOP_WORDS.add("<");
        SPECIAL_STOP_WORDS.add("&");
        SPECIAL_STOP_WORDS.add("\"");
        SPECIAL_STOP_WORDS.add("\t");
        SPECIAL_STOP_WORDS.add("!");
        SPECIAL_STOP_WORDS.add("#");
        SPECIAL_STOP_WORDS.add("$");
        SPECIAL_STOP_WORDS.add("%");
        SPECIAL_STOP_WORDS.add("'");
        SPECIAL_STOP_WORDS.add("(");
        SPECIAL_STOP_WORDS.add(")");
        SPECIAL_STOP_WORDS.add("*");
        SPECIAL_STOP_WORDS.add("+");
        SPECIAL_STOP_WORDS.add(",");
        SPECIAL_STOP_WORDS.add("-");
        SPECIAL_STOP_WORDS.add(".");
        SPECIAL_STOP_WORDS.add("/");
        SPECIAL_STOP_WORDS.add(":");
        SPECIAL_STOP_WORDS.add(";");
        SPECIAL_STOP_WORDS.add("=");

        SPECIAL_STOP_WORDS.add("?");
        SPECIAL_STOP_WORDS.add("@");
        SPECIAL_STOP_WORDS.add("[");
        SPECIAL_STOP_WORDS.add("/");
        SPECIAL_STOP_WORDS.add("]");
        SPECIAL_STOP_WORDS.add("^");

        SPECIAL_STOP_WORDS.add("_");
        SPECIAL_STOP_WORDS.add("`");
        SPECIAL_STOP_WORDS.add("{");
        SPECIAL_STOP_WORDS.add("|");
        SPECIAL_STOP_WORDS.add("}");
        SPECIAL_STOP_WORDS.add("~");



    }

}
