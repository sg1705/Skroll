package com.skroll.util;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by saurabhagarwal on 1/11/15.
 */
public class Model implements Serializable {

    public static final double PRIOR_COUNT = 100;
    public static final int DEFAULT_NUMBER_CATEGORIES = 2;
    public static final int DEFAULT_NUMBER_FEATURES = 0;

    int numberCategories;
    int numberFeatures;
    int[] featureSizes;
    int[] categoryCount;


    int totalCategoryCount; // do we need to use long instead of int?
    // int or long is better than double for increment by 1's used to update counts

    //int [][] featureCounts; // count
    List<List<int[]>> categoryFeatureValueCounts;
    Map<String,Integer>[] wordCounts;
}
