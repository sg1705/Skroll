package com.skroll.model;

import java.util.List;

/**
 * Created by saurabh on 12/22/14.
 */
public class Paragraph {

    private String id;
    private String text;
    private List<String> words;
    private List<String> definitions;

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

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public Paragraph(String id, String text) {
        this.id = id;
        this.text = text;
    }


}
