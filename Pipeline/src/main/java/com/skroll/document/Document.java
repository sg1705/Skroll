package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;

import java.util.List;

/**
 * Created by saurabh on 12/29/14.
 */
public class Document extends CoreMap {

//    String id;
    String source;
    String target;
//    List<CoreMap> paragraphs;


    public Document() {
        super();
    }

    public Document(String source) {
        this.source = source;
        initialize();

    }

    public List<CoreMap> getParagraphs() {
        List<CoreMap> paras = this.get(CoreAnnotations.ParagraphsAnnotation.class);
        return this.get(CoreAnnotations.ParagraphsAnnotation.class);
    }

    public void setParagraphs(List<CoreMap> paragraphs) {
        this.set(CoreAnnotations.ParagraphsAnnotation.class, paragraphs);
        //this.paragraphs = paragraphs;
    }

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



//    public String getId() {
//        return this.map.get(CoreAnnotations.DocumentIdAnnotation.class);
//        return id;
//    }

//    public void setId(String id) {
//        this.id = id;
//    }

}
