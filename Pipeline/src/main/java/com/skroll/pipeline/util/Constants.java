package com.skroll.pipeline.util;


import java.nio.charset.Charset;

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

    public static final int DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS = 6;

    public static final double DEF_THRESHOLD_PROBABILITY = 0.85;


    // extractor url
    public static final String PHANTOM_JS_BIN = "src/main/resources/parser/extractor/phantomjs";
    public static final String PHANTOM_JS_BIN_WINDOWS = "src/main/resources/parser/extractor/phantomjs.exe";
    public static final String PHANTOM_JS_BIN_MAC = "src/main/resources/parser/extractor/phantomjs_mac";
    public static final String JQUERY_PARSER_JS = "src/main/resources/parser/extractor/parser.js";

    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    // Logging markers
    public static final String LINKER_MARKER = "LINK_MARKER";
}
