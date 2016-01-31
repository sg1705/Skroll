package com.skroll.document;

import com.skroll.BaseTest;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Utils;
import org.junit.Test;


public class DocumentHelperTest extends BaseTest {

    @Test
    public void testAreDocumentsEqual() throws Exception {
        String fileName = "src/test/resources/document/simple-html-text.html";
        String htmlString = Utils.readStringFromFile(fileName);
        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = parser.parseDocumentFromHtml(htmlString);
        htmlDoc.setId(fileName);
        Document doc = parser.reParse(htmlDoc);
        doc.setId(fileName);
        assert (DocumentHelper.areDocumentsEqual(htmlDoc, doc));

        doc.setSource("");
        assert (!DocumentHelper.areDocumentsEqual(htmlDoc, doc));
    }

    @Test
    public void isObserved() throws Exception {
        String fileName = "src/test/resources/document/simple-html-text.html";
        String htmlString = Utils.readStringFromFile(fileName);
        Document document = parser.parseDocumentFromHtml(htmlString);//,"simple-html-text.html" );
        document.getParagraphs().get(0).set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        assert(DocumentHelper.isObserved(document));
    }
}
