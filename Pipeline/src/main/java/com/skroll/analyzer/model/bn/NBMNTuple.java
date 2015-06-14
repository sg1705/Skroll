package com.skroll.analyzer.model.bn;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 1/5/2015.
 */
public class NBMNTuple {

    List<String[]> wordsList;
    int categoryValue;
    int[] featureValues;
    int[] multiNodeValues;
    int[] docFeatureValues;

    public String[] getWords(int i) {
        return wordsList.get(i);
    }

    public int getCategoryValue() {
        return categoryValue;
    }

    public int[] getFeatureValues() {
        return featureValues;
    }

    public int[] getMultiNodeValues() {
        return multiNodeValues;
    }

    public int[] getDocFeatureValues() {
        return docFeatureValues;
    }

    public List<String[]> getWordsList() {
        return wordsList;
    }

    public NBMNTuple(List<String[]> words, int catgoryValue, int[] featureValues, int[] multiNodeValues, int[] docFeatureValues) {
        this.wordsList = words;
        this.categoryValue = catgoryValue;
        this.featureValues = featureValues;
        this.multiNodeValues = multiNodeValues;
        this.docFeatureValues = docFeatureValues;
    }

    @Override
    public String toString() {
        String wordsListString = "";
        for (String[] words : wordsList)
            wordsListString += Arrays.toString(words) + "\n  ";
        return "\nSimpleDataTuple{\n" +
                " words=\n  " + wordsListString +
                ", featureValues=" + Arrays.toString(featureValues) +
                ", multiNodeValues=" + Arrays.toString(multiNodeValues) +
                ", docFeatureValues=" + Arrays.toString(docFeatureValues) +

                '}';
    }

}
