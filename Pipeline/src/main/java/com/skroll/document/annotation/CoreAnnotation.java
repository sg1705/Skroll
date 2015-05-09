package com.skroll.document.annotation;

import com.skroll.analyzer.model.RandomVariableType;

/**
 * Created by saurabh on 1/3/15.
 */
public interface CoreAnnotation<V> extends TypesafeMap.Key<V> {

    public Class<V> getType();

}
