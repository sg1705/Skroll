package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.*;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.List;

/**
 * need to link RVs with it's sources
 * need to group RVs by its usages
 * Created by wei on 5/11/15.
 */
public class ModelRVSetting {

    public static final int NUM_WORDS_TO_USE_PER_PARAGRAPH = 10;
    @JsonProperty("nbfcConfig")
    NBFCConfig nbfcConfig;
    @JsonProperty("wordType")
    RandomVariable wordType;
    @JsonProperty("wordFeatures")
    List<RandomVariable> wordFeatures;

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    @JsonProperty("categoryId")
    int categoryId=0;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    @JsonProperty("categoryName")
    String categoryName="";

    /**
     * Defines the model
     * @param wordFeatures features for each. For example, if the word is in quota. Used at HMM level.
     * @param paraFeatureVars features for the paragraph
     * @param paraDocFeatureVars paragraph that are considered at doc level
     * @param wordVars type of words. For example - first words, unique words etc.
     * @param categoryId
     */

    public ModelRVSetting(
                          List<RandomVariable> wordFeatures,
                          List<RandomVariable> paraFeatureVars,
                          List<RandomVariable> paraDocFeatureVars,
                          List<RandomVariable> wordVars,
                          int categoryId,
                          String categoryName) {
        RandomVariable wordType = RVCreater.createWordLevelRVWithComputer(new WordIsInCategoryComputer(categoryId), "wordIsInCategory-" + categoryId);
        RandomVariable paraType = RVCreater.createDiscreteRVWithComputer(new ParaInCategoryComputer(categoryId), "paraTypeIsCategory-" + categoryId);
        nbfcConfig = new NBFCConfig(paraType, paraFeatureVars, paraDocFeatureVars,
                RVCreater.createDocFeatureRVs(paraDocFeatureVars,categoryName), wordVars);
        RVValues.addValueSetter(paraType, new RVValueSetter(categoryId, CoreAnnotations.CategoryAnnotations.class));
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.categoryId=categoryId;
        this.categoryName=categoryName;
    }


    public ModelRVSetting(
            @JsonProperty("nbfcConfig") NBFCConfig nbfcConfig,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("categoryId") int categoryId,
            @JsonProperty("categoryName") String categoryName) {
        this.nbfcConfig = nbfcConfig;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.categoryId = categoryId;
        this.categoryName=categoryName;
    }

    public NBFCConfig getNbfcConfig() {
        return nbfcConfig;
    }

    public RandomVariable getWordType() {
        return wordType;
    }

    public List<RandomVariable> getWordFeatures() {
        return wordFeatures;
    }
}
