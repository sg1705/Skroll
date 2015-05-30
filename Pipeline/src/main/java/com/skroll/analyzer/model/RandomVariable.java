package com.skroll.analyzer.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Arrays;
import java.util.List;

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
    public RandomVariable(
            @JsonProperty("featureSize")int featureSize,
            @JsonProperty("valueNames")String[] valueNames,
            @JsonProperty("name")String name) {
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

//    public boolean equals(RandomVariable var) {
//        boolean isEqual = true;
//        isEqual = isEqual && this.getName().equals(var.getName());
//        isEqual = isEqual && (this.getFeatureSize() == var.getFeatureSize());
//        return isEqual;
//    }

    public static boolean compareRVList(List<RandomVariable> list, List<RandomVariable> list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        boolean isEqual = true;
        for(int ii = 0; ii < list.size(); ii++) {
            isEqual = isEqual && list.get(ii).equals(list2.get(ii));
        }
        return isEqual;
    }


    public static boolean compareRVList(RandomVariable[] list, RandomVariable[] list2) {
        if (list.length != list2.length) {
            return false;
        }
        boolean isEqual = true;
        for(int ii = 0; ii < list.length; ii++) {
            isEqual = isEqual && list[ii].equals(list2[ii]);
        }
        return isEqual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RandomVariable that = (RandomVariable) o;

        if (getFeatureSize() != that.getFeatureSize()) return false;
        return named.equals(that.named);

    }

    @Override
    public int hashCode() {
        int result = named.hashCode();
        result = 31 * result + getFeatureSize();
        return result;
    }
}
