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
        Document doc = factory.get("d629534d10k.htm");
        assert (doc != null);
        System.out.println(doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
    }

    @Test
    public void testGetFromBENCHMARK() throws Exception {
        Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        DocumentFactory factory = new DocumentFactory(configuration);
        Document doc = factory.get("d629534d10k.htm");
        assert (doc != null);
        System.out.println(doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
    }

    @Test
    public void testPutDocument() throws Exception {
        Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        DocumentFactory factory = new DocumentFactory(configuration);
        Document doc = factory.get("d629534d10k.htm");
        factory.putDocument("xyz", doc);
        assert (factory.get("xyz") != null);
        factory.saveDocument(doc);
        File f = new File(configuration.get("preEvaluatedFolder", "/tmp/") + "xyz");
        assert (f.exists());

    }
}