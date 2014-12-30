package com.skroll.document;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class Entity {

    String id;
    String text;

    public Entity(String id, String text) {
        this.id = id;
        this.text = text;
    }

    List<Token> tokens;
    HashMap<EntityType, Entity> childEntities;


    public Entity() {

    }

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

    public boolean hasChildEntity(EntityType type) {
        if (this.childEntities == null) {
            return false;
        }
        Entity entity = childEntities.get(type);
        if (entity != null) {
            return true;
        } else {
            return false;
        }
    }

    public void addChildEntity(EntityType type, Entity entity) {
        if (this.childEntities == null) {
            this.childEntities = new HashMap<EntityType, Entity>();
        }
        this.childEntities.put(type,entity);
    }

    public Entity getChildEntity(EntityType type) {
        return childEntities.get(type);
    }

}
