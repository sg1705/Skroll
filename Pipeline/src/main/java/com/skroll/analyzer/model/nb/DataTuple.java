package com.skroll.analyzer.model.nb;

/**
 * Created by wei2learn on 1/5/2015.
 */
public class DataTuple {

    int category;
    String[] tokens;
    int[] features;

    public String[] getTokens() {
        return tokens;
    }

    public int[] getFeatures() {
        return features;
    }

    public int getCategory() {
        return category;
    }


    public DataTuple(int category, String[] tokens, int[] features) {
        this.category = category;
        this.tokens = tokens;
        this.features = features;
    }


}
