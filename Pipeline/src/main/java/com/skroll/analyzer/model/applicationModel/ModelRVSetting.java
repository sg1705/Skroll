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

    public int getClassifierId() {
        return classifierId;
    }

    public void setClassifierId(int classifierId) {
        this.classifierId = classifierId;
    }

    @JsonProperty("classifierId")
    int classifierId=0;

    public String getClassifierName() {
        return classifierName;
    }

    public void setClassifierName(String classifierName) {
        this.classifierName = classifierName;
    }

    @JsonProperty("classifierName")
    String classifierName="";

    @JsonProperty("numOfCategory")
    int numOfCategory =0;

    /**
     * Defines the model
     * @param wordFeatures features for each. For example, if the word is in quota. Used at HMM level.
     * @param paraFeatureVars features for the paragraph
     * @param paraDocFeatureVars paragraph that are considered at doc level
     * @param wordVars type of words. For example - first words, unique words etc.
     * @param classifierId
     */

    public ModelRVSetting(
                          List<RandomVariable> wordFeatures,
                          List<RandomVariable> paraFeatureVars,
                          List<RandomVariable> paraDocFeatureVars,
                          List<RandomVariable> wordVars,
                          int classifierId,
                          String classifierName,
                          int numOfCategory) {
        RandomVariable wordType = RVCreater.createWordLevelRVWithComputer(new WordIsInCategoryComputer(classifierId), "wordIsInCategory-" + classifierId);
        RandomVariable paraType = RVCreater.createDiscreteRVWithComputer(new ParaCategoryComputer(classifierId,numOfCategory), "paraTypeIsCategory-" + classifierId);
        nbmnConfig = new NBMNConfig(paraType, paraFeatureVars, paraDocFeatureVars,
                RVCreater.createNBMNDocFeatureRVs(paraDocFeatureVars, paraType, classifierName), wordVars);
        RVValues.addValueSetter(paraType, new RVValueSetter(classifierId, CoreAnnotations.CategoryAnnotations.class));
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.classifierId=classifierId;
        this.classifierName=classifierName;
        this.numOfCategory = numOfCategory;
    }


    public ModelRVSetting(
            @JsonProperty("nbmnConfig") NBMNConfig nbmnConfig,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("classifierId") int classifierId,
            @JsonProperty("classifierName") String classifierName,
            @JsonProperty("numOfCategory") int numOfCategory) {
        this.nbmnConfig = nbmnConfig;
        this.wordType = wordType;
        this.wordFeatures = wordFeatures;
        this.classifierId = classifierId;
        this.classifierName=classifierName;
        this.numOfCategory = numOfCategory;
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
