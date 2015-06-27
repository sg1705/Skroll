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
public class BenchmarkFSDocumentFactoryImpl extends
        FileSystemDocumentFactoryImpl {

    CacheService<Document> documentCache;

    protected ConcurrentHashSet<String> saveLaterDocumentIds = new ConcurrentHashSet<>();

    @Override
    protected CacheService<Document>  getDocumentCache() {
        return documentCache;
    }

    @Override
    protected ConcurrentHashSet<String> getSaveLaterDocumentId() {
        return saveLaterDocumentIds;
    }
    @Inject
    public BenchmarkFSDocumentFactoryImpl(Configuration configuration) {
        this.configuration = configuration;
        this.folder = configuration.get("benchmarkFolder");
        documentCache = new CacheService(this,0);
    }



}
