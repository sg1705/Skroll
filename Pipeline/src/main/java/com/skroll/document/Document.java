package com.skroll.document;

import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class Document {

    String id;
    String source;
    String target;
    List<Entity> fragments;

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }



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
