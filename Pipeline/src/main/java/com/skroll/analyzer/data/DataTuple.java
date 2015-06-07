package com.skroll.analyzer.data;

import java.util.List;

/**
 * Created by wei on 5/3/15.
 */
public class DataTuple {
    List<String[]> wordsList;
    List<int[]> valuesList;

    public String[] getWords(int i) {
        return wordsList.get(i);
    }

    public int[] getValues(int i) {
        return valuesList.get(i);
    }

}
