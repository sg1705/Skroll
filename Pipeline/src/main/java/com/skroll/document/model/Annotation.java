package com.skroll.document.model;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;

/**
 * Created by saurabh on 1/3/15.
 */
public class Annotation extends CoreMap {

    public Annotation() {
    }


    public Annotation(String text) {
        this.set(CoreAnnotations.TextAnnotation.class, text);
    }

}
