package com.skroll.document.factory;

import com.google.common.cache.*;
import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by saurabhagarwal on 6/27/15.
 */
public class CacheService<T> extends CacheLoader<String, T> implements RemovalListener<String,T>{

    public static final Logger logger = LoggerFactory.getLogger(FileSystemDocumentFactoryImpl.class);
    private static final int CACHE_SIZE = 10;
    private CacheHandler<T> cacheHandler;
    LoadingCache<String, T> loadingCache;
    CacheStats cacheStats = null;
    @Inject
    public CacheService(CacheHandler cacheHandler, int cacheSize){
        this.cacheHandler = cacheHandler;
        loadingCache = CacheBuilder.newBuilder().recordStats().maximumSize(cacheSize).removalListener(this).build(this);
        cacheStats = loadingCache.stats();
    }

    public CacheService(CacheHandler cacheHandler){
        this.cacheHandler = cacheHandler;
        loadingCache = CacheBuilder.newBuilder().recordStats().maximumSize(CACHE_SIZE).removalListener(this).build(this);
        cacheStats = loadingCache.stats();
    }

    public LoadingCache<String, T> getLoadingCache() {
        return loadingCache;
    }

    @Override
    public T load(String key) throws Exception {
        return cacheHandler.load(key);
    }

    @Override
    public void onRemoval(RemovalNotification<String, T> removal) {
        cacheHandler.onRemoval(removal.getKey(),removal.getValue());
    }

    public void logCacheStats(){
        CacheStats delta = loadingCache.stats().minus(cacheStats);
        cacheStats = loadingCache.stats();
        logger.info("Cumulative Cache stats: hitCount: {}, missCount: {}, loadSuccessCount: {}, loadExceptionCount: {}, totalLoadTime: {}",
                cacheStats.hitCount(),
                cacheStats.missCount(),
                cacheStats.loadSuccessCount(),
                cacheStats.loadExceptionCount(),
                cacheStats.totalLoadTime()
                );
        logger.info("Delta Cache stats: hitCount: {}, missCount: {}, loadSuccessCount: {}, loadExceptionCount: {}, totalLoadTime: {}",
                delta.hitCount(),
                delta.missCount(),
                delta.loadSuccessCount(),
                delta.loadExceptionCount(),
                delta.totalLoadTime());
    }
}
