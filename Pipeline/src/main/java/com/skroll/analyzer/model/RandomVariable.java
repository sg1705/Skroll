package com.skroll.analyzer.model;

import java.util.Arrays;

/**
 * Created by wei on 4/28/15.
 */
public class RandomVariable {
    String name = "";
    private int featureSize;
    private String[] valueNames;

    public RandomVariable() {
    }

    public RandomVariable(int featureSize) {
        this.featureSize = featureSize;
    }

    public RandomVariable(int featureSize, String name) {
        this.featureSize = featureSize;
        this.name = name;
    }

    public RandomVariable(int featureSize, String[] valueNames, String name) {
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

    @Override
    public String toString() {
        return "RandomVariable{" +
                "name='" + name + '\'' +
                ", featureSize=" + featureSize +
                ", valueNames=" + Arrays.toString(valueNames) +
                '}';
    }
}
