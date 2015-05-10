package com.skroll.document;

import junit.framework.TestCase;
import java.util.List;

public class DocumentTest extends TestCase {

    public void testGetParagraphs() throws Exception {
        String htmlText = "<div>First Paragraph</div>";
        Document doc = new Document(htmlText);
        assert(doc.getParagraphs().size() == 0);
    }

    public void testGetTables() throws Exception {
        String htmlText = "<div>First Paragraph</div>";
        Document doc = new Document(htmlText);
        List<CoreMap> tables = doc.getTables();
        assert (tables.size() == 0);
    }

    public void testGetSource() throws Exception {
        String htmlText = "<div>First Paragraph</div>";
        Document doc = new Document(htmlText);
        assert (doc.getSource().equals(htmlText));
    }

    public void testGetTarget() throws Exception {
        String htmlText = "<div>First Paragraph</div>";
        Document doc = new Document(htmlText);
        doc.setTarget(htmlText);
        assert (doc.getTarget().equals(htmlText));
    }
}