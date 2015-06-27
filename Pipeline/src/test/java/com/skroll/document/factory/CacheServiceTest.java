package com.skroll.document.factory;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.Document;
import com.skroll.util.Configuration;
import com.skroll.util.TestConfiguration;
import org.junit.Before;
import org.junit.Test;

public class CacheServiceTest {

    protected DocumentFactory factory;
    protected Configuration configuration;
    protected CacheService<Document> cacheService;
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
            cacheService = new CacheService((CorpusFSDocumentFactoryImpl)factory);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testGetLoadingCache() throws Exception {
        cacheService.load("d629534d10k.htm");
        assert (cacheService.getLoadingCache().get("d629534d10k.htm") != null);
    }
}