package com.skroll.analyzer.model.bn.config;

import com.skroll.analyzer.model.RandomVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei on 4/16/15.
 */
public class NBFCConfig extends NBConfig {
    List<RandomVariable> featureExistsAtDocLevelVarList;
    List<RandomVariable> documentFeatureVarList;

    List<RandomVariable> allParagraphFeatures;


    /**
     * Config for a classifier
     * @param categoryVar category random variable
     * @param featureVarList features for the category that is not used at document level.
     *                       For example, token count is not used at document level.
     * @param featureExistsAtDocLevelVarList  paragraph features that are considered at doc level
     * @param documentFeatureVarList features considered at doc level.
     * @param wordVarList
     */
    public NBFCConfig(RandomVariable categoryVar,
                      List<RandomVariable> featureVarList,
                      List<RandomVariable> featureExistsAtDocLevelVarList,
                      List<RandomVariable> documentFeatureVarList,
                      List<RandomVariable> wordVarList) {
        super(categoryVar, featureVarList, wordVarList);
        this.featureExistsAtDocLevelVarList = featureExistsAtDocLevelVarList;
        this.documentFeatureVarList = documentFeatureVarList;

        putAllParagraphFeaturesInOneList();
    }

    public List<RandomVariable> getFeatureExistsAtDocLevelVarList() {
        return featureExistsAtDocLevelVarList;
    }

    public List<RandomVariable> getDocumentFeatureVarList() {
        return documentFeatureVarList;
    }

    private void putAllParagraphFeaturesInOneList() {
        allParagraphFeatures = new ArrayList<>(featureVarList);
        allParagraphFeatures.addAll(featureExistsAtDocLevelVarList);
    }

    public List<RandomVariable> getAllParagraphFeatures() {
        return allParagraphFeatures;
    }
}
