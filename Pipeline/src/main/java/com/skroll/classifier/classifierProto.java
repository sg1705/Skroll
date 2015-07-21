package com.skroll.classifier;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by saurabhagarwal on 7/19/15.
 */

public class ClassifierProto {
    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("categoryIds")
    private List<Integer> categoryIds = null;

    public ClassifierProto(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("categoryIds") List<Integer> categoryIds) {
        this.id = id;
        this.name = name;
        this.categoryIds = categoryIds;
    }

    public int getId() {
        return id;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }

    public String getName() {
        return name;
    }
}
