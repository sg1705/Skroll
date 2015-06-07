package com.skroll.analyzer.model.bn;

import java.util.List;

/**
 * Created by wei on 5/3/15.
 */
public class NBTuple {

    List<String[]> words;
    int[] values;

    public String[] getWords(int i) {
        return words.get(i);
    }

    public String[] getWordsList(int i) {
        return words.get(i);
    }


    public int[] getDiscreteValues() {
        return values;
    }

    NBTuple() {

    }

    public NBTuple(List<String[]> words, int[] values) {
        this.words = words;
        this.values = values;
    }

}
