package com.skroll.document;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabh on 12/22/14.
 */
public class Paragraph {

    private String id;
    private String text;
    private List<String> tokens;
    private List<String> definitions;

    private HashMap<EntityType,NamedEntity> entities;

    private class NamedEntity {
        List<String> entityTokens;
    }


    public List<String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
    }

    private boolean isDefinition;

    public boolean isDefinition() {
        return isDefinition;
    }

    public void setDefinition(boolean isDefinition) {
        this.isDefinition = isDefinition;
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

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public Paragraph(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public NamedEntity getEntity(EntityType type) {
        return entities.get(type);
    }

}
