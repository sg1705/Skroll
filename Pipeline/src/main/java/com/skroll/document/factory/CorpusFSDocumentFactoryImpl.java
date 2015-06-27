package com.skroll.document.factory;

import com.skroll.document.Document;
import com.skroll.util.Configuration;
import org.eclipse.jetty.util.ConcurrentHashSet;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by saurabh on 6/14/15.
 */
@Singleton
public class CorpusFSDocumentFactoryImpl extends
        FileSystemDocumentFactoryImpl {

    protected CacheService<Document> documentCache;

    protected CacheService<Document> getDocumentCache() {
        return documentCache;
    }

    static ConcurrentHashSet<String> saveLaterDocumentIds= new ConcurrentHashSet<>();;

    @Override
    protected ConcurrentHashSet<String> getSaveLaterDocumentId() {
        return saveLaterDocumentIds;
    }

    @Inject
    public CorpusFSDocumentFactoryImpl(Configuration configuration) {
        this.configuration = configuration;
        this.folder = configuration.get("preEvaluatedFolder");
        int cacheSize = Integer.parseInt(configuration.get("preEvaluatedDocumentCacheSize", "10"));
        documentCache = new CacheService(this,cacheSize);
        logger.info(folder);
    }


}
