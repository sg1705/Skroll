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

    @Inject
    public CacheService(CacheHandler cacheHandler, int cacheSize){
        this.cacheHandler = cacheHandler;
        loadingCache = CacheBuilder.newBuilder().maximumSize(cacheSize).removalListener(this).build(this);
    }

    public CacheService(CacheHandler cacheHandler){
        this.cacheHandler = cacheHandler;
        loadingCache = CacheBuilder.newBuilder().maximumSize(CACHE_SIZE).removalListener(this).build(this);
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
}
