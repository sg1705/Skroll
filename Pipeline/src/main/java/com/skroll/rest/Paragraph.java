package com.skroll.rest;

import java.util.Collections;
import java.util.List;

public class Paragraph {

    private String paragraphId;
    private List<String> definedTerms;


    public Paragraph(String paragraphId, List<String> definedTerms) {
        this.paragraphId = paragraphId;
        this.definedTerms = Collections.unmodifiableList(definedTerms);
    }

    @Override
    public String toString() {
        return new StringBuffer(" paragraphId : ").append(this.paragraphId)
                .append(" definedTerms : ").append(this.definedTerms)
               .toString();
    }

}
