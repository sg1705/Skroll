package com.skroll.document.factory;

import com.google.common.cache.CacheBuilder;
import com.skroll.util.Configuration;

import javax.inject.Inject;

/**
 * Created by saurabh on 6/14/15.
 */
public class CorpusFSDocumentFactoryImpl extends
        FileSystemDocumentFactoryImpl {

    @Inject
    public CorpusFSDocumentFactoryImpl(Configuration configuration) {
        this.configuration = configuration;
        this.folder = configuration.get("preEvaluatedFolder");
        int cacheSize = Integer.parseInt(configuration.get("preEvaluatedDocumentCacheSize", "10"));
        this.documents = CacheBuilder.newBuilder().maximumSize(cacheSize).build(loader);
        logger.info(folder);
    }

}
