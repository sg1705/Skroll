package com.skroll.model;

import java.util.List;

/**
 * Created by saurabh on 12/22/14.
 */
public class HtmlDocument {

    private List<Paragraph> paragraphs;
    private String annotatedHtml;

    public String getAnnotatedHtml() {
        return annotatedHtml;
    }

    public void setAnnotatedHtml(String annotatedHtml) {
        this.annotatedHtml = annotatedHtml;
    }

    public List<Paragraph> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<Paragraph> paragraphs) {
        this.paragraphs = paragraphs;
    }
}
