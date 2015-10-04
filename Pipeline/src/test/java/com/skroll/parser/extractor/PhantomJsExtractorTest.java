package com.skroll.parser.extractor;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

public class PhantomJsExtractorTest extends TestCase {

    @Test
    public void testPhantomJsExtract() throws Exception {
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        Document doc = new Document("<div><u>this is a awesome</u></div>");
        phantomJsExtractor.setFetchMode(FetchMode.FILE);
        phantomJsExtractor.setParseMode(ParseMode.FULL);
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
        assert(doc.getParagraphs().size() == 3);
    }


    @Test
    public void testPhantomJsNoScript() throws Exception {
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        phantomJsExtractor.setFetchMode(FetchMode.FILE);
        phantomJsExtractor.setParseMode(ParseMode.FULL);
        Document doc = new Document("<script>//this is a comment</script><div>this is a awesome</div>");
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
        assert(doc.getParagraphs().size() == 3);
    }


    @Test
    public void testPhantomJsExtractHtmlFile() throws Exception {
        String fileName = "src/test/resources/analyzer/hmmTrainingDocs/Tribune CA 2.html";
        String htmlText = Utils.readStringFromFile(fileName);
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        phantomJsExtractor.setFetchMode(FetchMode.FILE);
        phantomJsExtractor.setParseMode(ParseMode.FULL);
        Document doc = new Document(htmlText);
        doc = phantomJsExtractor.process(doc);
        int totalPara = doc.getParagraphs().size();
        CoreMap para = doc.getParagraphs().get(totalPara - 1);
        System.out.println(doc.getParagraphs().size());
        assert (doc.getParagraphs() != null);
        assert (doc.getParagraphs().size() == 6763);
    }

    @Test
    public void testPhantomJsSpace() throws Exception {
        String fileName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";
        String htmlText = Utils.readStringFromFile(fileName);
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        phantomJsExtractor.setFetchMode(FetchMode.FILE);
        phantomJsExtractor.setParseMode(ParseMode.FULL);
        Document doc = new Document(htmlText);
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
        assert (doc.getParagraphs() != null);
        assert (doc.getParagraphs().size() == 2137);
    }


}