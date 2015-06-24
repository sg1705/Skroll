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
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.IsBoldAnnotation.class),
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsAnchorAnnotation.class),
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsHrefAnnotation.class)
    );
    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new UniqueWordsComputer(), "uniqueWords"),
            RVCreater.createWordsRVWithComputer(new FirstWordsComputer(), "firstWord")
    );

    public TOCModelRVSetting(int categoryId, String categoryName) {
        super(  DEFAULT_WORD_FEATURES,
                DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS,
                DEFAULT_WORD_VARS,
                categoryId,
                categoryName);
    }
}
