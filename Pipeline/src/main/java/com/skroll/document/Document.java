package com.skroll.document;

import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class Document {

    String id;
    List<Entity> fragments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Entity> getFragments() {
        return fragments;
    }

    public void setFragments(List<Entity> fragments) {
        this.fragments = fragments;
    }
}
