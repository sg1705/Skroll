package com.skroll.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paragraph {
    public static final Logger logger = LoggerFactory
            .getLogger(Paragraph.class);

    private String paragraphId;
    private String definedTerm;


    public Paragraph(String paragraphId, String definedTerm) {
        this.paragraphId = paragraphId;
        this.definedTerm = definedTerm;
    }

    @Override
    public String toString() {
        return new StringBuffer(" paragraphId : ").append(this.paragraphId)
                .append(",").append(" definedTerm : ").append(definedTerm)
               .toString();
    }

}
