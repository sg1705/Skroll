package com.skroll.analyzer.model;

/**
 * Created by wei on 5/9/15.
 */
public interface ARVConverter {
    public RandomVariable getRV();

    public <V> int getValue(V value);
}
