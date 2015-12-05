package com.skroll.analyzer.model.bn.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.node.DiscreteNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 4/16/15.
 */
public class NBMNConfig extends NBConfig {

    @JsonProperty("featureExistsAtDocLevelVarList")
    List<RandomVariable> featureExistsAtDocLevelVarList;
    @JsonProperty("documentFeatureVarList")
    List<List<RandomVariable>> documentFeatureVarList;
    @JsonIgnore
    List<RandomVariable> allParagraphFeatures;


    /**
     * Config for a classifier
     *
     * @param categoryVar                    category random variable
     * @param featureVarList                 features for the category that is not used at document level.
     *                                       For example, token count is not used at document level.
     * @param featureExistsAtDocLevelVarList paragraph features that are considered at doc level
     * @param documentFeatureVarList         features considered at doc level.
     * @param wordVarList
     */
    public NBMNConfig(@JsonProperty("categoryVar") RandomVariable categoryVar,
                      @JsonProperty("featureVarList") List<RandomVariable> featureVarList,
                      @JsonProperty("featureExistsAtDocLevelVarList") List<RandomVariable> featureExistsAtDocLevelVarList,
                      @JsonProperty("documentFeatureVarList") List<List<RandomVariable>> documentFeatureVarList,
                      @JsonProperty("wordVarList") List<RandomVariable> wordVarList) {
        super(categoryVar, featureVarList, wordVarList);
        this.featureExistsAtDocLevelVarList = featureExistsAtDocLevelVarList;
        this.documentFeatureVarList = documentFeatureVarList;

        putAllParagraphFeaturesInOneList();
    }

    public List<RandomVariable> getFeatureExistsAtDocLevelVarList() {
        return featureExistsAtDocLevelVarList;
    }

    public List<List<RandomVariable>> getDocumentFeatureVarList() {
        return documentFeatureVarList;
    }

    private void putAllParagraphFeaturesInOneList() {
        allParagraphFeatures = new ArrayList<>(featureVarList);
        allParagraphFeatures.addAll(featureExistsAtDocLevelVarList);
    }

    public List<RandomVariable> getAllParagraphFeatures() {
        return allParagraphFeatures;
    }

    public boolean equals(NBMNConfig nbfc) {
        boolean isEquals = true;
        isEquals = isEquals && RandomVariable.compareRVList(this.featureExistsAtDocLevelVarList, nbfc.featureExistsAtDocLevelVarList);
        for (int f = 0; f < documentFeatureVarList.size(); f++)
            isEquals = isEquals && RandomVariable.compareRVList(
                    this.documentFeatureVarList.get(f), nbfc.documentFeatureVarList.get(f));
        isEquals = isEquals && RandomVariable.compareRVList(this.allParagraphFeatures, nbfc.allParagraphFeatures);
        return isEquals;
    }

    @Override
    public String toString() {
        return "NBMNConfig{" +
                "allParagraphFeatures=" + allParagraphFeatures +
                '}';
    }
}
