package com.skroll.document.annotation;

/**
 * Created by saurabh on 1/3/15.
 */
public interface CoreAnnotation<V> extends TypesafeMap.Key<V> {

    public Class<V> getType();
//    public Class getAnnType();
}
