package com.skroll.analyzer.model;

/**
 * Created by wei on 4/28/15.
 */
public class RandomVariable {
    String name = "";
    private int featureSize;
    private String[] valueNames;

    RandomVariable() {
    }

    RandomVariable(int featureSize) {
        this.featureSize = featureSize;
    }

    RandomVariable(int featureSize, String name) {
        this.featureSize = featureSize;
        this.name = name;
    }

    RandomVariable(int featureSize, String[] valueNames, String name) {
        this.featureSize = featureSize;
        this.valueNames = valueNames;
        this.name = name;
    }

    public int getFeatureSize() {
        return featureSize;
    }

    public String getValueName(int i) {
        if (valueNames == null) return String.valueOf(i);
        return valueNames[i];
    }

    public String getName() {
        return name;
    }

}
