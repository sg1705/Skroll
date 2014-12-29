package com.skroll.document;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class Entity {

    String id;
    String text;
    List<Token> tokens;
    HashMap<EntityType, Entity> childEntities;

    public HashMap<EntityType, Entity> getChildEntities() {
        return childEntities;
    }

    public void setChildEntities(HashMap<EntityType, Entity> childEntities) {
        this.childEntities = childEntities;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public void setTokens(List<Token> tokens) {
        this.tokens = tokens;
    }

}
