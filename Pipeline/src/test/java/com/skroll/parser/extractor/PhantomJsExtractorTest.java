package com.skroll.parser.extractor;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.benchmark.Benchmark;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.factory.BenchmarkFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.SkrollTestGuiceModule;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class PhantomJsExtractorTest {

    PhantomJsExtractor phantomJsExtractor;

    @Before
    public void setup() throws Exception {
        Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
        phantomJsExtractor = injector.getInstance(PhantomJsExtractor.class);
    }



    @Test
    public void testPhantomJsExtract() throws Exception {
        Document doc = new Document("<div><u>this is a awesome</u></div>");
        phantomJsExtractor.setFetchMode(FetchMode.FILE);
        phantomJsExtractor.setParseMode(ParseMode.FULL);
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
        assert(doc.getParagraphs().size() == 3);
    }


    @Test
    public void testPhantomJsNoScript() throws Exception {
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
//        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
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
//        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        phantomJsExtractor.setFetchMode(FetchMode.FILE);
        phantomJsExtractor.setParseMode(ParseMode.FULL);
        Document doc = new Document(htmlText);
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
        assert (doc.getParagraphs() != null);
        assert (doc.getParagraphs().size() == 2137);
    }


}