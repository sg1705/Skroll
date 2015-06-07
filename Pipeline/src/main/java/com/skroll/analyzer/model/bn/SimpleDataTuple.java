package com.skroll.analyzer.model.bn;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 1/5/2015.
 */
public class SimpleDataTuple {

    List<String[]> wordsList;
    int[] values;

    public String[] getWords(int i) {
        return wordsList.get(i);
    }

    public int[] getDiscreteValues() {
        return values;
    }

    public SimpleDataTuple(List<String[]> words, int[] values) {
        this.wordsList = words;
        this.values = values;
    }

    @Override
    public String toString() {
        String wordsListString = "";
        for (String[] words : wordsList)
            wordsListString += Arrays.toString(words) + "\n  ";
        return "\nSimpleDataTuple{\n" +
                " words=\n  " + wordsListString +
                " values=" + Arrays.toString(values) +
                '}';
    }
}
