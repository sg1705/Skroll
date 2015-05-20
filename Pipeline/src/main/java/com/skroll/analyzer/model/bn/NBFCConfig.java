//package com.skroll.analyzer.model.bn;
//
//import com.skroll.analyzer.model.RandomVariable;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by wei on 4/16/15.
// */
//public class NBFCConfig extends NBConfig{
//    List<RandomVariable> featureExistsAtDocLevelVarList;
//    List<RandomVariable> documentFeatureVarList;
//
//    List<RandomVariable> allParagraphFeatures;
//
//
//    public NBFCConfig(    RandomVariable categoryVar,
//                          List<RandomVariable> featureVarList,
//                          List<RandomVariable> featureExistsAtDocLevelVarList,
//                          List<RandomVariable> documentFeatureVarList,
//                          List<RandomVariable> wordVarList ){
//        super(categoryVar, featureVarList, wordVarList);
//        this.featureExistsAtDocLevelVarList = featureExistsAtDocLevelVarList;
//        this.documentFeatureVarList = documentFeatureVarList;
//
//        putAllParagraphFeaturesInOneList();
//    }
//
//    public List<RandomVariable> getFeatureExistsAtDocLevelVarList() {
//        return featureExistsAtDocLevelVarList;
//    }
//
//    public List<RandomVariable> getDocumentFeatureVarList() {
//        return documentFeatureVarList;
//    }
//
//    private void putAllParagraphFeaturesInOneList(){
//        allParagraphFeatures = new ArrayList<>(featureVarList);
//        allParagraphFeatures.addAll(featureExistsAtDocLevelVarList);
//    }
//
//    public List<RandomVariable> getAllParagraphFeatures() {
//        return allParagraphFeatures;
//    }
//}
