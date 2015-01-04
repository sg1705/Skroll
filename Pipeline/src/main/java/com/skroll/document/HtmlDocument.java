package com.skroll.document;

import java.util.List;

/**
 * Created by saurabh on 12/22/14.
 */
public class HtmlDocument {

    private List<CoreMap> paragraphs;
    private String annotatedHtml;


    public HtmlDocument() {
    }

    public HtmlDocument(String sourceHtml) {
        this.sourceHtml = sourceHtml;
    }

    private String sourceHtml;



    public String getSourceHtml() {
        return sourceHtml;
    }

    public void setSourceHtml(String sourceHtml) {
        this.sourceHtml = sourceHtml;
    }

    public String getAnnotatedHtml() {
        return annotatedHtml;
    }

    public void setAnnotatedHtml(String annotatedHtml) {
        this.annotatedHtml = annotatedHtml;
    }

    public List<CoreMap> getParagraphs() {
        return paragraphs;
    }

    public void setParagraphs(List<CoreMap> paragraphs) {
        this.paragraphs = paragraphs;
    }
}
