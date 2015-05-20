package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class Document extends CoreMap {

    String target;


    public Document() {
        super();
    }

    public Document(String source) {
        initialize();
        this.setSource(source);
    }

    public Document(CoreMap map) {
        this.map = map.map;
    }

    public List<CoreMap> getParagraphs() {
        if (this.containsKey(CoreAnnotations.ParagraphsAnnotation.class)) {
            return this.get(CoreAnnotations.ParagraphsAnnotation.class);
        } else {
            return new ArrayList<CoreMap>();
        }
    }

    public List<CoreMap> getTables() {
        if (this.containsKey(CoreAnnotations.TablesAnnotation.class)) {
            return this.get(CoreAnnotations.TablesAnnotation.class);
        } else {
            return new ArrayList<CoreMap>();
        }
    }


    public void setParagraphs(List<CoreMap> paragraphs) {
        this.set(CoreAnnotations.ParagraphsAnnotation.class, paragraphs);
    }

    public String getSource() {
        return getText();
    }

    public void setSource(String source) {
        this.set(CoreAnnotations.TextAnnotation.class, source);
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }


}
