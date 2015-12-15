package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.*;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.document.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/11/15.
 */
public class TOCModelRVSetting extends ModelRVSetting {

    NBMNConfig lowLevelNbmnConfig = null;
    List<Integer> lowLevelCategoryIds = null;
    double[] lowerAnnotatingThreshold = null;

    public double[] getLowerAnnotatingThreshold() {
        return lowerAnnotatingThreshold;
    }

    public void setLowerAnnotatingThreshold(double[] lowerAnnotatingThreshold) {
        this.lowerAnnotatingThreshold = lowerAnnotatingThreshold;
    }

    static final List<RandomVariable> DEFAULT_WORD_FEATURES = Arrays.asList(
            RVCreater.createRVFromAnnotation(CoreAnnotations.InQuotesAnnotation.class),
            RVCreater.createRVFromAnnotation(CoreAnnotations.IsUnderlineAnnotation.class)
    );
    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList(
            RVCreater.createDiscreteRVWithComputer(new NumberTokensComputer(), "numTokens"),
            RVCreater.createDiscreteRVWithComputer(new NotInTableRVComputer(), "notInTable_S"),
            RVCreater.createDiscreteRVWithComputer(new IsParaNumberComputer(), "isParaNumber")
//            RVCreater.createDiscreteRVWithComputer(new EndsWithAlphaWordComputer(), "endsWithAlphaWord"),
//            RVCreater.createDiscreteRVWithComputer(new IsParaTitleCaseComputer(), "isParaTitleCase")
//            RVCreater.createDiscreteRVWithComputer(new IsInUserTOCRVComputer(), "inUserTOC"),
//            RVCreater.createDiscreteRVWithComputer(new StartsWithNumberComputer(), "startsWithNumber")
//            RVCreater.createDiscreteRVWithComputer(new EndsWithNumberComputer(), "endsWithNumber"),
//            RVCreater.createDiscreteRVWithComputer(new EndsWithNumberComputer(), "paraEndsWithNumber")
    );
    static List<RandomVariable> DEFAULT_SHARED_PARA_FEATURE_VARS =
            RVCreater.addNegationRVs(
                    Arrays.asList(
                            RVCreater.createDiscreteRVWithComputer(new StartsWithNumberComputer(), "paraStartsWithNumber_S"),
                            RVCreater.createDiscreteRVWithComputer(new IsParaTitleCaseComputer(), "isParaTitleCase_S"),
                            RVCreater.createDiscreteRVWithComputer(new EndsWithAlphaWordComputer(), "endsWithAlphaWord_S"),
                            RVCreater.createDiscreteRVWithComputer(new IsSecondWordStartsWithNumberComputer(), "isSecondWordStartsWithNumber_S"),
                            RVCreater.createParagraphIsRV (CoreAnnotations.IsItalicAnnotation.class),
                            RVCreater.createParagraphIsRV(CoreAnnotations.IsUnderlineAnnotation.class),
                            RVCreater.createParagraphIsRV(CoreAnnotations.IsBoldAnnotation.class),
                            RVCreater.createRVFromAnnotation(CoreAnnotations.IsAnchorAnnotation.class),
                            RVCreater.createRVFromAnnotation(CoreAnnotations.IsHrefAnnotation.class),
                            RVCreater.createRVFromAnnotation(CoreAnnotations.IsUpperCaseAnnotation.class),
                            RVCreater.createRVFromAnnotation(CoreAnnotations.IsCenterAlignedAnnotation.class)

                    )
            );

    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new LowerCaseWordsComputer(), "lowerCaseWords"),
            RVCreater.createWordsRVWithComputer(new FirstWordComputer(), "firstWord"),
            RVCreater.createWordsRVWithComputer(new LastWordComputer(), "LastWord"),
            RVCreater.createWordsRVWithComputer(new LastAlphaWordComputer(), "lastAlphaWord")
    );

    public TOCModelRVSetting(List<Integer> categoryIds, double[] annotatingThreshold,
                             List<Integer> lowLevelCategoryIds, double[] lowerLevelAnnotatingThreshold) {
        this(DEFAULT_WORD_FEATURES,
                DEFAULT_PARA_FEATURE_VARS, DEFAULT_SHARED_PARA_FEATURE_VARS,
                DEFAULT_WORD_VARS,
                categoryIds,
                annotatingThreshold,
                lowLevelCategoryIds,
                lowerLevelAnnotatingThreshold
        );
    }

    public TOCModelRVSetting(
            List<RandomVariable> wordFeatures,
            List<RandomVariable> paraFeatureVars,
            List<RandomVariable> paraDocFeatureVars,
            List<RandomVariable> wordVars,
            List<Integer> categoryIds,
            double[] annotatingThreshold,
            List<Integer> lowLevelCategoryIds,
            double[] lowerLevelAnnotatingThreshold
    ) {
        super(wordFeatures,
                paraFeatureVars, paraDocFeatureVars,
                wordVars,
                categoryIds
        );
        this.lowLevelCategoryIds = lowLevelCategoryIds;
        setAnnotatingThreshold(annotatingThreshold);
        setLowerAnnotatingThreshold(lowerLevelAnnotatingThreshold);

        if (lowLevelCategoryIds != null) {
            RandomVariable lowLevelParaType = RVCreater.createDiscreteRVWithComputer(
                    new ParaCategoryComputer(modelClassAndWeightStrategy, lowLevelCategoryIds), "paraTypeIsModelID-" + lowLevelCategoryIds);
            lowLevelNbmnConfig = new NBMNConfig(lowLevelParaType, paraFeatureVars, paraDocFeatureVars,
                    RVCreater.createNBMNDocFeatureRVs(paraDocFeatureVars, lowLevelParaType, String.valueOf(lowLevelCategoryIds.toString())), wordVars);
            RVValues.addValueSetter(lowLevelParaType, new RVValueSetter(lowLevelCategoryIds, CoreAnnotations.CategoryAnnotations.class));
        }
    }

    @JsonCreator
    public TOCModelRVSetting(
            @JsonProperty("nbmnConfig") NBMNConfig nbmnConfig,
            @JsonProperty("lowerLevelNbmnConfig") NBMNConfig lowerLevelNbmnConfig,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("categoryIds") List<Integer> categoryIds,
            @JsonProperty("annotatingThreshold") double[] annotatingThreshold,
            @JsonProperty("lowLevelCategoryIds") List<Integer> lowLevelCategoryIds,
            @JsonProperty("lowerLevelAnnotatingThreshold") double[] lowerLevelAnnotatingThreshold
    ) {
        super(nbmnConfig, wordType, wordFeatures, categoryIds);
        this.lowLevelCategoryIds = lowLevelCategoryIds;
        setAnnotatingThreshold(annotatingThreshold);
        setLowerAnnotatingThreshold(lowerLevelAnnotatingThreshold);
    }

    @Override
    protected void initializeStrategies() {
//        this.postProcessFunctions.add(DocProcessor::annotateProcessParaWithTOCMatch);
        ManagedCategoryStrategy managedCategoryStrategy = new DefaultManagedCategoryStrategy();
        UnManagedCategoryStrategy unManagedCategoryStrategy = new DefaultUnManagedCategoryStrategy();
        this.modelClassAndWeightStrategy = new DefaultModelClassAndWeightStrategy(managedCategoryStrategy, unManagedCategoryStrategy);
    }


    public List<Integer> getLowLevelCategoryIds() {
        return lowLevelCategoryIds;
    }

    public NBMNConfig getLowLevelNbmnConfig() {
        return lowLevelNbmnConfig;
    }

}
