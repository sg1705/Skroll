package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.*;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/11/15.
 */
public class TOCModelRVSetting extends ModelRVSetting {

    static final String NAME = "toc";
    public static final RandomVariable PARA_IS_TOC =
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsTOCAnnotation.class);

    // todo: not used now. need to implement HMM for TOC later.
    static final RandomVariable WORD_IS_TOC =
            RVCreater.createWordLevelRVWithComputer(new WordIsDefComputer(), "wordIsTOC");

    static final List<RandomVariable> DEFAULT_WORD_FEATURES = Arrays.asList(
            RVCreater.createRVFromAnnotation(CoreAnnotations.InQuotesAnnotation.class),
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsUnderlineAnnotation.class)
    );


    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NumberTokensComputer(), "numTokens")
    );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NotInTableRVComputer(), "notInTable"),
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.IsItalicAnnotation.class),
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.IsUnderlineAnnotation.class),
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.IsBoldAnnotation.class)

    );

    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new UniqueWordsComputer(), "uniqueWords"),
            RVCreater.createWordsRVWithComputer(new FirstWordsComputer(), "firstWord")
    );


    public TOCModelRVSetting() {
        super(NAME, WORD_IS_TOC, DEFAULT_WORD_FEATURES,
                PARA_IS_TOC, DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS,
                DEFAULT_WORD_VARS);

        RVValues.addValueSetter(PARA_IS_TOC, new RVValueSetter(
                CoreAnnotations.IsTOCAnnotation.class,
                CoreAnnotations.TOCTokensAnnotation.class
        ));

    }


}
