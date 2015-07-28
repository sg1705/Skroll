package com.skroll.classifier;

import java.util.List;

/**
 * Created by saurabhagarwal on 7/19/15.
 */

public class ClassifierProto {

    private int id;
    private List<Integer> categoryIds = null;

    public ClassifierProto( int id, List<Integer> categoryIds) {
        this.id = id;
        this.categoryIds = categoryIds;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }
}
