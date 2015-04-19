package com.skroll.analyzer.model.bn;

import java.util.List;

/**
 * Created by wei2learn on 1/5/2015.
 */
public class SimpleDataTuple {

    List<String[]> words;
    int[] values;

    public String[] getWords(int i) {
        return words.get(i);
    }

    public int[] getDiscreteValues() {
        return values;
    }

    public SimpleDataTuple(List<String[]> words, int[] values) {
        this.words = words;
        this.values = values;
    }


}
