package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariableType;

import java.util.List;

/**
 * Created by wei on 4/16/15.
 */
public class NBFCConfig extends NBConfig{
    List<RandomVariableType> featureExistsAtDocLevelVarList;
    List<RandomVariableType> documentFeatureVarList;

    public NBFCConfig(    RandomVariableType categoryVar,
                          List<RandomVariableType> featureVarList,
                          List<RandomVariableType> featureExistsAtDocLevelVarList,
                          List<RandomVariableType> documentFeatureVarList,
                          List<RandomVariableType> wordVarList ){
        super(categoryVar, featureVarList, wordVarList);
        this.featureExistsAtDocLevelVarList = featureExistsAtDocLevelVarList;
        this.documentFeatureVarList = documentFeatureVarList;
    }

    public List<RandomVariableType> getFeatureExistsAtDocLevelVarList() {
        return featureExistsAtDocLevelVarList;
    }

    public List<RandomVariableType> getDocumentFeatureVarList() {
        return documentFeatureVarList;
    }
}
