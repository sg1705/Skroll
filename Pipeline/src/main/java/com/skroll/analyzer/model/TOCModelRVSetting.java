package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.NBFCConfig;
import com.skroll.document.annotation.CoreAnnotations;
import com.sun.org.apache.xpath.internal.operations.Mod;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/11/15.
 */
public class TOCModelRVSetting extends ModelRVSetting {

    static final RandomVariable DEFAULT_PARA_IS_TOC =
            RandomVariableCreater.createRVFromAnnotation(CoreAnnotations.IsTOCAnnotation.class);

    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(

    );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RandomVariableCreater.createParagraphStartsWithRV(CoreAnnotations.InQuotesAnnotation.class)
    );

    static final List<RandomVariable> DEFAULT_DOC_FEATURE_VARS = Arrays.asList(
            new RandomVariable(2, "tocs in quotes")
    );
    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList();

    public TOCModelRVSetting() {
        super(DEFAULT_PARA_IS_TOC, DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS,
                DEFAULT_DOC_FEATURE_VARS, DEFAULT_WORD_VARS);
    }


}
