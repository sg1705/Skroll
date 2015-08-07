package com.skroll.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class TermProto {

    private String paragraphId;
    private String term;
    private int classificationId;
    private boolean isUserObserved;

    public static final Logger logger = LoggerFactory
            .getLogger(TermProto.class);

    public String getParagraphId() {
        return paragraphId;
    }

    public String getTerm() {
        return term;
    }

    public TermProto(String paragraphId, String term, int classificationId, boolean isUserObserved) {
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


    public static Map<TermProto, List<String>> combineTerms(List<TermProto> termProtos) {

        Map<TermProto, List<String>> paraMap = new HashMap<>();

        for (TermProto termProto : termProtos) {
                if (paraMap.get(termProto) == null) {
                    paraMap.put(termProto, new ArrayList<String>());
                }
            paraMap.get(termProto).add(termProto.getTerm());
        }

        return paraMap;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TermProto)) return false;

        TermProto termProto = (TermProto) o;

        if (classificationId != termProto.classificationId) return false;
        if (!paragraphId.equals(termProto.paragraphId)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = paragraphId.hashCode();
        result = 31 * result + classificationId;
        return result;
    }

    public static class ParagraphComparator implements Comparator<TermProto> {

        @Override
        public int compare(TermProto o1, TermProto o2) {
            if (Integer.parseInt(o1.getParagraphId()) > Integer.parseInt(o2.getParagraphId())) {
                return 1;
            } else  if (Integer.parseInt(o1.getParagraphId()) < Integer.parseInt(o2.getParagraphId())) {
                return -1;
            }
            return 0;
        }
    }

}
