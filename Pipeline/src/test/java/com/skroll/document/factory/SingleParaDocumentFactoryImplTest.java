package com.skroll.document.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.skroll.document.Document;
import com.skroll.util.Configuration;
import com.skroll.util.TestConfiguration;
import com.skroll.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

public class SingleParaDocumentFactoryImplTest {

    Document document;
    protected DocumentFactory factory;
    protected Configuration configuration;
    protected DocumentFactory singleParaDocumentFactory;
    @Before
    public void setUp() throws Exception {
        try {
            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(DocumentFactory
                            .class)
                            .to(CorpusFSDocumentFactoryImpl.class);
                    bind(Configuration.class).to(TestConfiguration.class);
                    bind(DocumentFactory.class)
                            .annotatedWith(SingleParaFSDocumentFactory.class)
                            .to(SingleParaDocumentFactoryImpl.class);

                }
            });
            singleParaDocumentFactory = injector.getInstance(Key.get(DocumentFactory.class, SingleParaFSDocumentFactory.class));
            factory = injector.getInstance(DocumentFactory.class);
            configuration = injector.getInstance(Configuration.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        document = TestHelper.setUpTestDoc();
    }

    @Test
    public void testGetEntireDocCollaspedAsSingleParagraph() throws Exception {
        CorpusFSDocumentFactoryImpl corpusFSDocumentFactory = new CorpusFSDocumentFactoryImpl(configuration);
        corpusFSDocumentFactory.putDocument(document);
        corpusFSDocumentFactory.saveDocument(document);
        System.out.println("document:Number of Para:" + document.getParagraphs().size());
        List<Object> tokenStrings1 = document
                .getParagraphs()
                .stream().flatMap(paragraph -> paragraph.getTokens().stream()).collect(Collectors.toList());

        System.out.println("document:" +tokenStrings1);

        Document collaspedDocument = singleParaDocumentFactory.get(document.getId());
        System.out.println("collaspedDocument:Number of Para:" + collaspedDocument.getParagraphs().size());
        List<Object> tokenStrings2 = collaspedDocument
                .getParagraphs()
                .stream().flatMap(paragraph -> paragraph.getTokens().stream()).collect(Collectors.toList());
        System.out.println("collaspedDocument:" +tokenStrings2);
        assert(collaspedDocument.getParagraphs().size()==1);
    }
}