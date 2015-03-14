package com.skroll.analyzer.model.bn;

/**
 * Created by wei2learn on 1/5/2015.
 */
public class SimpleDataTuple {

    String[] words;
    int[] values;

    public String[] getWords() {
        return words;
    }

    public int[] getDiscreteValues() {
        return values;
    }

    public SimpleDataTuple(String[] words, int[] values) {
        this.words = words;
        this.values = values;
    }


}
