package com.skroll.document.factory;

/**
 * Created by saurabhagarwal on 6/27/15.
 */
public interface CacheHandler<T> {

    public T load(String key) throws Exception;

    public void onRemoval(String key, T value);

}
