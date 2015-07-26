package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.*;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.List;

/**
 * need to link RVs with it's sources
 * need to group RVs by its usages
 * Created by wei on 5/11/15.
 */
public class ModelRVSetting {

    public static final int NUM_WORDS_TO_USE_PER_PARAGRAPH = 20;
    @JsonProperty("nbmnConfig")
    NBMNConfig nbmnConfig;
    @JsonProperty("wordType")
    RandomVariable wordType;
    @JsonProperty("wordFeatures")
    List<RandomVariable> wordFeatures;
    @JsonProperty("modelName")
    String modelName;
    @JsonProperty("categoryIds")
    List<Integer> categoryIds=null;

    public String getModelName() {
        return modelName;
    }

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
                          String modelName,
                          List<Integer> categoryIds
                          ) {
        this.categoryIds=categoryIds;
        RandomVariable wordType = RVCreater.createWordLevelRVWithComputer(new WordIsInCategoryComputer(categoryIds), "wordIsInCategoryIDs-" + modelName);
        RandomVariable paraType = RVCreater.createDiscreteRVWithComputer(new ParaCategoryComputer(categoryIds), "paraTypeIsCategoryIDs-" + modelName);
        nbmnConfig = new NBMNConfig(paraType, paraFeatureVars, paraDocFeatureVars,
                RVCreater.createNBMNDocFeatureRVs(paraDocFeatureVars, paraType, modelName), wordVars);
        RVValues.addValueSetter(paraType, new RVValueSetter(categoryIds, CoreAnnotations.CategoryAnnotations.class));
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.modelName = modelName;

    }


    public ModelRVSetting(
            @JsonProperty("nbmnConfig") NBMNConfig nbmnConfig,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("modelName") String modelName,
            @JsonProperty("categoryIds") List<Integer> categoryIds) {
        this.nbmnConfig = nbmnConfig;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.modelName = modelName;
        this.categoryIds = categoryIds;
    }

    public NBMNConfig getNbmnConfig() {
        return nbmnConfig;
    }

    public RandomVariable getWordType() {
        return wordType;
    }

    public List<RandomVariable> getWordFeatures() {
        return wordFeatures;
    }


}
