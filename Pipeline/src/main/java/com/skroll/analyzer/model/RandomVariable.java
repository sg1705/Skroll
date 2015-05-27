package com.skroll.analyzer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Arrays;

/**
 * Created by wei on 4/28/15.
 */

public class RandomVariable {

    @JsonProperty("named")
    String named = "";

    @JsonProperty("featureSize")
    private int featureSize;

    @JsonProperty("valueNames")
    private String[] valueNames;

    @JsonIgnore
    public RandomVariable() {
    }

    @JsonIgnore
    public RandomVariable(int featureSize) {
        this.featureSize = featureSize;
    }

    @JsonIgnore
    public RandomVariable(int featureSize, String name) {
        this.featureSize = featureSize;
        this.named = name;
    }

    @JsonCreator
    public RandomVariable(int featureSize, String[] valueNames, String name) {
        this.featureSize = featureSize;
        this.valueNames = valueNames;
        this.named = name;
    }

    @JsonIgnore
    public int getFeatureSize() {
        return featureSize;
    }

    @JsonIgnore
    public String getValueName(int i) {
        if (valueNames == null) return String.valueOf(i);
        return valueNames[i];
    }

    @JsonIgnore
    public String getName() {
        return named;
    }

    @Override
    @JsonIgnore
    public String toString() {
        return "RandomVariable{" +
                "name='" + named + '\'' +
                ", featureSize=" + featureSize +
                ", valueNames=" + Arrays.toString(valueNames) +
                '}';
    }
}
