package com.skroll.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Paragraph {
    public static final Logger logger = LoggerFactory
            .getLogger(Paragraph.class);

    public String getParagraphId() {
        return paragraphId;
    }

    public String getDefinedTerm() {
        return definedTerm;
    }

    private String paragraphId;
    private String definedTerm;
    private String TOCTerm;

    public Paragraph(String paragraphId, String definedTerm) {
        this.paragraphId = paragraphId;
        this.definedTerm = definedTerm;
    }

    @Override
    public String toString() {
        return new StringBuffer(" paragraphId : ").append(this.paragraphId)
                .append(",").append(" definedTerm : ").append(definedTerm).append(",").append(" TOCTerm : ").append(TOCTerm)
               .toString();
    }

    public String getTOCTerm() {
        return TOCTerm;
    }

    public void setTOCTerm(String TOCTerm) {
        this.TOCTerm = TOCTerm;
    }
}
