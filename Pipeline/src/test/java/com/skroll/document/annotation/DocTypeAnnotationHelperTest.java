package com.skroll.document.annotation;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.skroll.document.Document;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.document.factory.SingleParaDocumentFactoryImpl;
import com.skroll.document.factory.SingleParaFSDocumentFactory;
import com.skroll.util.Configuration;
import com.skroll.util.TestConfigWithPreEvalFolder;
import com.skroll.util.TestConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DocTypeAnnotationHelperTest {

    Document document;
    DocumentFactory corpusDocumentFactory;
    DocumentFactory singleParaDocumentFactory;
    Configuration configuration;

    public static final Logger logger = LoggerFactory
            .getLogger(CategoryAnnotationHelperTest.class);
    @Before
    public void setUp() throws Exception {
        //document = Parser.parseDocumentFromHtmlFile("src/test/resources/classifier/smaller-indenture.html");
            try {

                Injector injector = Guice.createInjector(new AbstractModule() {
                    @Override
                    protected void configure() {
                        bindConstant().annotatedWith(TestConfigWithPreEvalFolder.Location.class).to("src/test/resources/document/documentFactory/");
                        bind(Configuration.class).to(TestConfiguration.class);
                        bind(DocumentFactory.class)
                                .to(CorpusFSDocumentFactoryImpl.class);
                        bind(DocumentFactory.class)
                                .annotatedWith(SingleParaFSDocumentFactory.class)
                                .to(SingleParaDocumentFactoryImpl.class);
                    }
                });

                corpusDocumentFactory = injector.getInstance(DocumentFactory.class);
                configuration = injector.getInstance(Configuration.class);
                singleParaDocumentFactory = injector.getInstance(Key.get(DocumentFactory.class, SingleParaFSDocumentFactory.class));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    @Test
    public void testAnnotateDocTypeWithWeightAndUserObservation() throws Exception {
        document = corpusDocumentFactory.get("d629534d10k.htm");
        DocTypeAnnotationHelper.annotateDocTypeWithWeightAndUserObservation(document,101,01f);
        logger.info("updateDocType {} using document id {}", 101, document.getId());
        logger.info("DocType:" + DocTypeAnnotationHelper.getDocType(document));
        assert(DocTypeAnnotationHelper.getDocType(document)==101);
    }
}