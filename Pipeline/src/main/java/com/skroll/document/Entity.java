package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TypesafeMap;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by saurabh on 12/29/14.
 */
public class Entity implements TypesafeMap {


    String id;
    String text;
    HashMap map;

    public Entity(String id, String text) {
        map = new HashMap();
        this.id = id;
        this.set(CoreAnnotations.TextAnnotation.class, text);
    }

    public Entity() {
        map = new HashMap();
    }


    public String getId() {
        return id;
    }


    //two helper methods
    public String getText() {
        return this.get(CoreAnnotations.TextAnnotation.class);
    }

    public List<Token> getTokens() {
        return this.get(CoreAnnotations.TokenAnnotation.class);
    }


    @Override
    public <VALUE> boolean has(Class<? extends TypesafeMap.Key<VALUE>> key) {
        return map.containsKey(key);
    }

    @Override
    public <VALUE> VALUE get(Class<? extends TypesafeMap.Key<VALUE>> key) {
        return (VALUE)map.get(key);
    }

    @Override
    public <VALUE> VALUE set(Class<? extends TypesafeMap.Key<VALUE>> key, VALUE value) {
        this.map.put(key, value);
        return value;
    }

    @Override
    public <VALUE> VALUE remove(Class<? extends TypesafeMap.Key<VALUE>> key) {
        VALUE value = (VALUE)this.map.get(key);
        this.map.remove(key);
        return value;
    }

    @Override
    public Set<Class<?>> keySet() {
        return this.map.keySet();
    }

    @Override
    public <VALUE> boolean containsKey(Class<? extends TypesafeMap.Key<VALUE>> key) {
        return this.map.containsKey(key);
    }

    @Override
    public int size() {
        return this.map.size();
    }






//
//
//
//
//
//
//
//    public HashMap<EntityType, Entity> getChildEntities() {
//        return childEntities;
//    }
//
//    public void setChildEntities(HashMap<EntityType, Entity> childEntities) {
//        this.childEntities = childEntities;
//    }
//
//    public String getId() {
//        return id;
//    }
//
//    public void setId(String id) {
//        this.id = id;
//    }
//
//    public String getText() {
//        return text;
//    }
//
//    public void setText(String text) {
//        this.text = text;
//    }
//
//    public List<Token> getTokens() {
//        return tokens;
//    }
//
//    public void setTokens(List<Token> tokens) {
//        this.tokens = tokens;
//    }
//
//    public boolean hasChildEntity(EntityType type) {
//        if (this.childEntities == null) {
//            return false;
//        }
//        Entity entity = childEntities.get(type);
//        if (entity != null) {
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    public <VALUE> void addChildEntity(Class<? extends TypesafeMap.Key<VALUE>> type, VALUE value) {
//        if (this.childEntities == null) {
//            this.childEntities = new HashMap<EntityType, Entity>();
//        }
//        this.childEntities.put(type,value);
//    }
//
//    public Entity getChildEntity(EntityType type) {
//        return childEntities.get(type);
//    }

}
