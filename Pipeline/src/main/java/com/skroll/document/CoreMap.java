package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TypesafeMap;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by saurabh on 12/29/14.
 */
public class CoreMap implements TypesafeMap  {

    HashMap map;

    public CoreMap(String id, String text) {
        initialize();
        this.set(CoreAnnotations.TextAnnotation.class, text);
        this.set(CoreAnnotations.IdAnnotation.class, id);
    }

    public CoreMap() {
        initialize();
    }

    protected void initialize() {
        this.map = new HashMap();
    }


    public String getId() {
        return this.get(CoreAnnotations.IdAnnotation.class);
    }

    public void setId(String id) {
        this.set(CoreAnnotations.IdAnnotation.class, id);
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
        return map.containsKey(key.getSimpleName());
    }

    @Override
    public <VALUE> VALUE get(Class<? extends TypesafeMap.Key<VALUE>> key) {
        VALUE value = (VALUE)(this.map.get(key.getName()));
        //return (VALUE)(this.map.get(key.getSimpleName()));
        return (VALUE)(this.map.get(CoreAnnotations.aMap.get(key)));
    }

    @Override
    public <VALUE> VALUE set(Class<? extends TypesafeMap.Key<VALUE>> key, VALUE value) {
        //this.map.put(key.getSimpleName(), value);
        this.map.put(CoreAnnotations.aMap.get(key), value);
        return value;
    }


    public <VALUE> VALUE set(String key, VALUE value) {
        this.map.put(key, value);
        return value;
    }



    @Override
    public <VALUE> VALUE remove(Class<? extends TypesafeMap.Key<VALUE>> key) {
        VALUE value = (VALUE)this.map.get(key.getSimpleName());
        this.map.remove(key.getSimpleName());
        return value;
    }

    @Override
    public Set<Class<?>> keySet() {
        return this.map.keySet();
    }


    public Set<String> keyNameSet() {
        return this.map.keySet();
    }


    @Override
    public <VALUE> boolean containsKey(Class<? extends TypesafeMap.Key<VALUE>> key) {
        return this.map.containsKey(CoreAnnotations.aMap.get(key));
        //return this.map.containsKey(key.getSimpleName());
    }

    @Override
    public int size() {
        return this.map.size();
    }

    public <VALUE> String keyName(Class<? extends TypesafeMap.Key<VALUE>> key) {
        return key.getSimpleName();
    }

    public boolean equals(CoreMap coremap) {
        Set<String> keys = this.keyNameSet();
        Set<String> nKeys = coremap.keyNameSet();
        if (keys.size() != nKeys.size()) {
            return false;
        }
        //iterate over each keys
        for (String key : keys) {
            boolean contains = nKeys.contains(key);
            if (!contains) {
                return false;
            }
        }
        //iterate over each keys
        for (String key : nKeys) {
            boolean contains = keys.contains(key);
            if (!contains) {
                return false;
            }
        }
        return true;
    }

}
