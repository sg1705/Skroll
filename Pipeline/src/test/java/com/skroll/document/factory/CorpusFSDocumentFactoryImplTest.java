package com.skroll.document.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.util.Configuration;
import com.skroll.util.TestConfiguration;
import org.junit.Before;
import org.junit.Test;
import java.io.File;


public class CorpusFSDocumentFactoryImplTest {

    protected DocumentFactory factory;
    protected Configuration configuration;

    @Before
    public void setUp() throws Exception {
        try {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(DocumentFactory.class)
                            .to(CorpusFSDocumentFactoryImpl.class);

                    bind(Configuration.class).to(TestConfiguration.class);
                }
            });

            factory = injector.getInstance(DocumentFactory.class);
            configuration = injector.getInstance(Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testIsDocumentExist() throws Exception {
        assert (factory.isDocumentExist("d629534d10k.htm"));
    }

    @Test
    public void testGet() throws Exception {
        Document doc = factory.get("d629534d10k.htm");
        assert (doc != null);
        System.out.println(doc.get(CoreAnnotations.ParserVersionAnnotationInteger.class));
        System.out.println(doc.getParagraphs().size());
    }

    @Test
    public void testPutDocument() throws Exception {
        Document doc = factory.get("d629534d10k.htm");
        factory.putDocument("xyz", doc);
        assert (factory.get("xyz") != null);
        factory.saveDocument(doc);
        assert(isDocExist(doc));
    }


    @Test(expected=Exception.class)
    public void testPutDocumentWithNullId() throws Exception {
        Document doc = Parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);
        factory.putDocument(null, doc);
    }


    @Test(expected=Exception.class)
    public void testSaveDocumentWithNullId() throws Exception {
        Document doc = Parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);
        factory.saveDocument(doc);
    }

    public void testSaveDocument() throws Exception {
        Document doc = Parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        // this doc has three paragraphs
        doc.setId("testId");
        assert (doc.getParagraphs().size() == 3);
        factory.saveDocument(doc);
        assert (isDocExist(doc));

    }

    private boolean isDocExist(Document doc) {
        File f = new File(configuration.get("preEvaluatedFolder", "/tmp/") + doc.getId());
        return f.exists();
    }

}