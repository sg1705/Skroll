package com.skroll.analyzer.model;

import com.skroll.document.annotation.CoreAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/11/15.
 */
public class TOCModelRVSetting {

    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList();
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RandomVariableCreater.createParagraphStartsWithRV(CoreAnnotations.InQuotesAnnotation.class));

    static final List<RandomVariable> DEFAULT_DOC_FEATURE_VARS = Arrays.asList();
    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList();


    List<RandomVariable> paraFeatureVars = DEFAULT_PARA_FEATURE_VARS;
    List<RandomVariable> paraDocFeatureVars = DEFAULT_PARA_DOC_FEATURE_VARS;
    List<RandomVariable> docFeatureVars = DEFAULT_DOC_FEATURE_VARS;
    List<RandomVariable> wordVars = DEFAULT_WORD_VARS;
}
