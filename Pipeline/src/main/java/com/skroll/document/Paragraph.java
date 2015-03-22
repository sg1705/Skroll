package com.skroll.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paragraph {

    private String paragraphId;
    private String term;
    private int classificationId;

    public final static int NONE_CLASSIFICATION = 0;
    public final static int DEFINITION_CLASSIFICATION = 1;
    public final static int TOC_CLASSIFICATION = 2;

    public static final Logger logger = LoggerFactory
            .getLogger(Paragraph.class);

    public String getParagraphId() {
        return paragraphId;
    }

    public String getTerm() {
        return term;
    }

    public Paragraph(String paragraphId, String definedTerm, int classificationId) {
        this.paragraphId = paragraphId;
        this.term = definedTerm;
        this.classificationId = classificationId;
    }

    @Override
    public String toString() {
        return new StringBuffer(" paragraphId : ").append(this.paragraphId)
                .append(",").append(" definedTerm : ").append(term).append(",").append(" TOCTerm : ").append(classificationId)
               .toString();
    }

    public int getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(int classificationId) {
        this.classificationId = classificationId;
    }
}
