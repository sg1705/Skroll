package com.skroll.document;

import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.Configuration;
import org.junit.Test;

import javax.inject.Named;

import static org.junit.Assert.*;

public class DocumentFactoryTest {

    @Test
    public void testGet() throws Exception {
        Configuration configuration = new Configuration("src/test/resources/skroll-test.properties");
        DocumentFactory factory = new DocumentFactory(configuration);
        Document doc = factory.get("d629534d10k.htm");
        assert (doc != null);
        System.out.println(doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
    }

    @Test
    public void testPutDocument() throws Exception {

    }
}