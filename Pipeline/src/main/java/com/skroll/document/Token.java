package com.skroll.document;

import com.google.common.base.Strings;
import com.skroll.document.annotation.CoreAnnotations;

/**
 * Created by saurabh on 12/29/14.
 */
public class Token extends CoreMap {

    public Token() {
        super();
    }

    public Token(String text) {
        this.set(CoreAnnotations.TextAnnotation.class, text);
    }



    public String getText() {
        return this.get(CoreAnnotations.TextAnnotation.class);
    }

    public void setText(String text) {
        this.set(CoreAnnotations.TextAnnotation.class, text);
    }

    public String toString() {
        return getText();
    }


}
