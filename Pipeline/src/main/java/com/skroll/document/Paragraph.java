package com.skroll.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class Paragraph {

    private String paragraphId;
    private String term;
    private int classificationId;
    private boolean isUserObserved;

    public static final Logger logger = LoggerFactory
            .getLogger(Paragraph.class);

    public String getParagraphId() {
        return paragraphId;
    }

    public String getTerm() {
        return term;
    }

    public Paragraph(String paragraphId, String term, int classificationId, boolean isUserObserved) {
        this.paragraphId = paragraphId;
        this.term = term;
        this.classificationId = classificationId;
        this.isUserObserved = isUserObserved;
    }

    @Override
    public String toString() {
        return new StringBuffer(" paragraphId : ").append(this.paragraphId)
                .append(",").append(" term : ").append(term).append(",").append(" ClassificationId : ").append(classificationId)
               .toString();
    }

    public int getClassificationId() {
        return classificationId;
    }

    public void setClassificationId(int classificationId) {
        this.classificationId = classificationId;
    }


    public static Map<Paragraph, List<String>> combineTerms(List<Paragraph> paragraphs) {

        Map<Paragraph, List<String>> paraMap = new HashMap<>();

        for (Paragraph paragraph : paragraphs) {
                if (paraMap.get(paragraph) == null) {
                    paraMap.put(paragraph, new ArrayList<String>());
                }
            paraMap.get(paragraph).add(paragraph.getTerm());
        }

        return paraMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Paragraph)) return false;

        Paragraph paragraph = (Paragraph) o;

        if (classificationId != paragraph.classificationId) return false;
        if (!paragraphId.equals(paragraph.paragraphId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paragraphId.hashCode();
        result = 31 * result + classificationId;
        return result;
    }

    public static class ParagraphComparator implements Comparator<Paragraph> {

        @Override
        public int compare(Paragraph o1, Paragraph o2) {
            if (Integer.parseInt(o1.getParagraphId()) > Integer.parseInt(o2.getParagraphId())) {
                return 1;
            } else  if (Integer.parseInt(o1.getParagraphId()) < Integer.parseInt(o2.getParagraphId())) {
                return -1;
            }
            return 0;
        }
    }

}
