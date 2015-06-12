package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.Configuration;
import org.junit.Test;

import java.io.File;

public class DocumentFactoryTest {

    @Test
    public void testGetFromPRE_EVALUATED_FOLDER() throws Exception {
        Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        DocumentFactory factory = new DocumentFactory(configuration);
        Document doc = factory.get(DocumentFactory.PRE_EVALUATED_FOLDER,"d629534d10k.htm");
        assert (doc != null);
        System.out.println(doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
    }
    @Test
    public void testGetFromBENCHMARK() throws Exception {
        Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        DocumentFactory factory = new DocumentFactory(configuration);
        Document doc = factory.get(DocumentFactory.BENCHMARK,"d629534d10k.htm");
        assert (doc != null);
        System.out.println(doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
    }
    @Test
    public void testGetFromTEST() throws Exception {
        Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        DocumentFactory factory = new DocumentFactory(configuration);
        Document doc = factory.get(DocumentFactory.TEST,"d629534d10k.htm");
        assert (doc != null);
        System.out.println(doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
    }


    @Test
    public void testPutDocument() throws Exception {
        Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        DocumentFactory factory = new DocumentFactory(configuration);
        Document doc = factory.get(DocumentFactory.TEST,"d629534d10k.htm");
        factory.putDocument(DocumentFactory.TEST, "xyz", doc);
        assert (factory.get(DocumentFactory.TEST,"xyz")!=null);
        factory.saveDocument(DocumentFactory.TEST, doc);
        File f = new File(configuration.get(DocumentFactory.TEST, "/tmp/") + "xyz");
        assert (f.exists());

    }
}