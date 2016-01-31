package com.skroll.document.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.BaseTest;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.util.Configuration;
import com.skroll.util.TestConfiguration;
import org.junit.Before;
import org.junit.Test;


public class CorpusFSDocumentFactoryImplTest extends BaseTest {

    protected DocumentFactory factory;
    protected DocumentFactory factory_for_SingletonTest;
    protected Configuration configuration;

    @Before
    public void setUp() throws Exception {
        try {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(Configuration.class).to(TestConfiguration.class);
                    bind(DocumentFactory.class)
                            .to(CorpusFSDocumentFactoryImpl.class);


                }
            });

            factory = injector.getInstance(DocumentFactory.class);
            factory_for_SingletonTest = injector.getInstance(DocumentFactory.class);
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
    public void testSaveOnCacheEviction() throws Exception {
        Document doc1 = parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        Document doc2 = parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        // this doc has three paragraphs
        doc1.setId("testId1");
        factory.putDocument(doc1);
        factory.putDocument(doc1);
        doc2.setId("testId2");
        factory.putDocument(doc2);
        assert(((CorpusFSDocumentFactoryImpl)factory).getDocumentCache().getLoadingCache().size()==1);
        assert(((CorpusFSDocumentFactoryImpl)factory).getSaveLaterDocumentId().contains("testId2"));

        assert(factory.getDocumentIds().contains("testId1"));
        assert(factory.getDocumentIds().contains("testId2"));
    }

    @Test
    public void testSingleton() throws Exception {
        Document doc1 = parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        Document doc2 = parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        // this doc has three paragraphs
        doc1.setId("testId1");
        factory.putDocument(doc1);
        doc2.setId("testId2");
        factory.putDocument(doc2);
        assert(((CorpusFSDocumentFactoryImpl)factory).getDocumentCache().getLoadingCache().size()==1);
        assert(((CorpusFSDocumentFactoryImpl)factory_for_SingletonTest).getDocumentCache().getLoadingCache().size()==1);
        assert(factory.equals(factory_for_SingletonTest));
    }

    @Test
    public void testGetNonExistDoc() throws Exception {
        Document doc = factory.get("xyz123");
        assert(doc==null);
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
        Document doc = parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        doc.setId("xyz");
        factory.putDocument(doc);
        assert (factory.get("xyz") != null);
        factory.saveDocument(doc);
        assert(factory.isDocumentExist(doc.getId()));
    }


    @Test(expected=Exception.class)
    public void testPutDocumentWithNullId() throws Exception {
        Document doc = parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);
        factory.putDocument(doc);
    }


    @Test(expected=Exception.class)
    public void testSaveDocumentWithNullId() throws Exception {
        Document doc = parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        // this doc has three paragraphs
        assert (doc.getParagraphs().size() == 3);
        factory.saveDocument(doc);
    }

    @Test
    public void testSaveDocument() throws Exception {
        Document doc = parser.parseDocumentFromHtml("<div><u>this is a awesome</u></div>" +
                "<div>This is second paragraph</div>" +
                "<div>This is third paragraph</div");
        // this doc has three paragraphs
        doc.setId("testId");
        assert (doc.getParagraphs().size() == 3);
        factory.saveDocument(doc);
        assert (factory.isDocumentExist(doc.getId()));

    }

}
