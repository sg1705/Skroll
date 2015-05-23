package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.*;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/11/15.
 */
public class DefModelRVSetting extends ModelRVSetting {

    public static final RandomVariable PARA_IS_DEF =
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsDefinitionAnnotation.class);
    static final RandomVariable WORD_IS_DEF =
            RVCreater.createWordLevelRVWithComputer(new WordIsDefComputer(), "wordIsDef");

    static final List<RandomVariable> DEFAULT_WORD_FEATURES = Arrays.asList(
            RVCreater.createRVFromAnnotation(CoreAnnotations.InQuotesAnnotation.class),
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsUnderlineAnnotation.class)
    );


    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NumberTokensComputer(), "numTokens")
    );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NotInTableRVComputer(), "notInTable"),
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.InQuotesAnnotation.class),
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.IsItalicAnnotation.class),
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.IsUnderlineAnnotation.class),
            RVCreater.createParagraphStartsWithRV(CoreAnnotations.IsBoldAnnotation.class)

    );

    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new UniqueWordsComputer(), "uniqueWords")
//            RVCreater.createRVFromAnnotation(CoreAnnotations.WordSetForTrainingAnnotation.class)
    );


    public DefModelRVSetting() {
        super(WORD_IS_DEF, DEFAULT_WORD_FEATURES,
                PARA_IS_DEF, DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS, DEFAULT_WORD_VARS);

        RVValues.addValueSetter(PARA_IS_DEF, new RVValueSetter(
                CoreAnnotations.IsDefinitionAnnotation.class,
                CoreAnnotations.DefinedTermTokensAnnotation.class
        ));
    }


}
