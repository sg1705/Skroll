package com.skroll.analyzer.data;

import com.skroll.analyzer.model.bn.SimpleDataTuple;
import com.skroll.analyzer.model.bn.config.NBFCConfig;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class NBFCData {
    int[][] paraFeatures;
    int[][] paraDocFeatures;
    List<String[]>[] wordsLists;
    NBFCConfig config;
    SimpleDataTuple[] tuples;
    int[] docFeatureValues;

//    public DocData(Document doc, NBFCConfig config) {
//        this.config = config;
//        tuples = new SimpleDataTuple[doc.getParagraphs().size()];
//    }


    public void setParaFeatures(int[][] paraFeatures) {
        this.paraFeatures = paraFeatures;
    }

    public void setParaDocFeatures(int[][] paraDocFeatures) {
        this.paraDocFeatures = paraDocFeatures;
    }

    public void setWordsLists(List<String[]>[] wordsLists) {
        this.wordsLists = wordsLists;
    }

    public int[][] getParaFeatures() {
        return paraFeatures;
    }

    public int[][] getParaDocFeatures() {
        return paraDocFeatures;
    }

    public void setDocFeatureValues(int[] docFeatureValues) {
        this.docFeatureValues = docFeatureValues;
    }

    public List<String[]>[] getWordsLists() {
        return wordsLists;
    }

    public NBFCConfig getConfig() {
        return config;
    }

    @Override
    public String toString() {
        String wordsString = "\n";
        for (List<String[]> wordsList : wordsLists) {
            for (String[] words : wordsList) {
                wordsString += Arrays.toString(words);
            }
            wordsString += "\n";
        }

        return "NBFCData{" +
                "paraFeatures=" + Arrays.deepToString(paraFeatures) +
                ", paraDocFeatures=" + Arrays.deepToString(paraDocFeatures) +
                ", wordsLists=" + wordsString +
                ", config=" + config +
                '}';
    }
}
