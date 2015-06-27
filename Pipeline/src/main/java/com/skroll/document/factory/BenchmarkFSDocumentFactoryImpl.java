package com.skroll.document.factory;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.skroll.document.Document;
import com.skroll.util.Configuration;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabh on 6/14/15.
 */
public class BenchmarkFSDocumentFactoryImpl extends
        FileSystemDocumentFactoryImpl {

    @Singleton
    LoadingCache<String, Document> documentCache;
    @Override
    protected LoadingCache<String, Document> getDocumentCache() {
        return documentCache;
    }

    @Singleton
    List<String> saveLaterDocumentIds = new ArrayList<>();
    @Override
    protected List<String> getSaveLaterDocumentId() {
        return saveLaterDocumentIds;
    }
    @Inject
    public BenchmarkFSDocumentFactoryImpl(Configuration configuration) {
        this.configuration = configuration;
        this.folder = configuration.get("benchmarkFolder");
        documentCache = CacheBuilder.newBuilder().maximumSize(0).build(loader);
    }



}
