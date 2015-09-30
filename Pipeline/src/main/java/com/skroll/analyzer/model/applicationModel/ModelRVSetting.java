package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.*;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.*;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.ModelClassAndWeightStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * need to link RVs with it's sources
 * need to group RVs by its usages
 * Created by wei on 5/11/15.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefModelRVSetting.class, name = "DefModelRVSetting"),
        @JsonSubTypes.Type(value = TOCModelRVSetting.class, name = "TOCModelRVSetting"),
        @JsonSubTypes.Type(value = DocTypeModelRVSetting.class, name = "DocTypeModelRVSetting")})

public class ModelRVSetting {

    public static final int NUM_WORDS_TO_USE_PER_PARAGRAPH = Integer.MAX_VALUE;
    @JsonProperty("nbmnConfig")
    NBMNConfig nbmnConfig;
    NBMNConfig lowLevelNbmnConfig;
    @JsonProperty("wordType")
    RandomVariable wordType;
    @JsonProperty("wordFeatures")
    List<RandomVariable> wordFeatures;
    @JsonProperty("categoryIds")
    List<Integer> categoryIds=null;
    List<Integer> lowLevelCategoryIds = null;

    /* Various strategies for the model */
    @JsonIgnore
    protected List<BiFunction<List<CoreMap>, List<CoreMap>, Void>> postProcessFunctions
            = new ArrayList<>();

    @JsonIgnore
    protected ModelClassAndWeightStrategy modelClassAndWeightStrategy;

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    /**
     * Defines the model
     * @param wordFeatures features for each. For example, if the word is in quota. Used at HMM level.
     * @param paraFeatureVars features for the paragraph
     * @param paraDocFeatureVars paragraph that are considered at doc level
     * @param wordVars type of words. For example - first words, unique words etc.
     * @param categoryIds
     */

    public ModelRVSetting(
                          List<RandomVariable> wordFeatures,
                          List<RandomVariable> paraFeatureVars,
                          List<RandomVariable> paraDocFeatureVars,
                          List<RandomVariable> wordVars,
                          List<Integer> categoryIds,
                          List<Integer> lowLevelategoryIds

                          ) {
        initializeStrategies();
        this.categoryIds=categoryIds;
        this.lowLevelCategoryIds = lowLevelCategoryIds;
        RandomVariable wordType = RVCreater.createWordLevelRVWithComputer(new WordIsInCategoryComputer(modelClassAndWeightStrategy, categoryIds), "wordIsInModelID-" + categoryIds);
        RandomVariable paraType = RVCreater.createDiscreteRVWithComputer(new ParaCategoryComputer(modelClassAndWeightStrategy,categoryIds), "paraTypeIsModelID-" + categoryIds);
        nbmnConfig = new NBMNConfig(paraType, paraFeatureVars, paraDocFeatureVars,
                RVCreater.createNBMNDocFeatureRVs(paraDocFeatureVars, paraType, String.valueOf(categoryIds.toString())), wordVars);
        RVValues.addValueSetter(paraType, new RVValueSetter(categoryIds, CoreAnnotations.CategoryAnnotations.class));

        if (lowLevelategoryIds != null) {
            RandomVariable lowLevelParaType = RVCreater.createDiscreteRVWithComputer(
                    new ParaCategoryComputer(modelClassAndWeightStrategy, lowLevelategoryIds), "paraTypeIsModelID-" + lowLevelategoryIds);
            lowLevelNbmnConfig = new NBMNConfig(lowLevelParaType, paraFeatureVars, paraDocFeatureVars,
                    RVCreater.createNBMNDocFeatureRVs(paraDocFeatureVars, lowLevelParaType, String.valueOf(lowLevelategoryIds.toString())), wordVars);
            RVValues.addValueSetter(lowLevelParaType, new RVValueSetter(lowLevelategoryIds, CoreAnnotations.CategoryAnnotations.class));
        }

        this.wordType = wordType;
        this.wordFeatures = wordFeatures;

    }


    @JsonCreator
    public ModelRVSetting(
            @JsonProperty("nbmnConfig") NBMNConfig nbmnConfig,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("categoryIds") List<Integer> categoryIds,
            @JsonProperty("lowLevelCategoryIds") List<Integer> lowLevelCategoryIds
    ) {
        initializeStrategies();
        this.nbmnConfig = nbmnConfig;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.categoryIds = categoryIds;
        this.lowLevelCategoryIds = lowLevelCategoryIds;
    }

    /**
     * Initialize all the strategies for the model
     */
    protected void initializeStrategies() {

    }

    public NBMNConfig getNbmnConfig() {
        return nbmnConfig;
    }

    public List<Integer> getLowLevelCategoryIds() {
        return lowLevelCategoryIds;
    }

    public NBMNConfig getLowLevelNbmnConfig() {
        return lowLevelNbmnConfig;
    }

    public RandomVariable getWordType() {
        return wordType;
    }

    public List<RandomVariable> getWordFeatures() {
        return wordFeatures;
    }


}
