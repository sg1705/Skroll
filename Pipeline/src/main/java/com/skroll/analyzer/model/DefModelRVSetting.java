package com.skroll.analyzer.model;

import com.skroll.document.annotation.CoreAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/11/15.
 */
public class DefModelRVSetting extends ModelRVSetting {

    static final RandomVariable DEFAULT_PARA_IS_DEF =
            RandomVariableCreater.createRVFromAnnotation(CoreAnnotations.IsTOCAnnotation.class);

    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(
            RandomVariableCreater.createDiscreteRVWithComputer(new NumberTokensComputer(), "numTokens")
    );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RandomVariableCreater.createParagraphStartsWithRV(CoreAnnotations.InQuotesAnnotation.class)
    );

    static final List<RandomVariable> DEFAULT_DOC_FEATURE_VARS = Arrays.asList(
            new RandomVariable(2, "tocs in quotes")
    );
    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RandomVariableCreater.createWordsRVWithComputer(new UniqueWordsComputer(), "uniqueWords")
//            RandomVariableCreater.createRVFromAnnotation(CoreAnnotations.WordSetForTrainingAnnotation.class)
    );

    public DefModelRVSetting() {
        super(DEFAULT_PARA_IS_DEF, DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS,
                DEFAULT_DOC_FEATURE_VARS, DEFAULT_WORD_VARS);
    }


}
