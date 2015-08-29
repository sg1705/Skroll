package com.skroll.classifier;

import com.google.common.base.Objects;

import java.util.List;

/**
 * Created by saurabhagarwal on 7/19/15.
 */

public class ClassifierProto {

    private ClassifierId id;
    private List<Integer> categoryIds = null;

    public ClassifierProto( ClassifierId id, List<Integer> categoryIds) {
        this.id = id;
        this.categoryIds = categoryIds;
    }

    public ClassifierId getId() {
        return id;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("categoryIds", categoryIds)
                .add("id", id)
                .toString();
    }
}
