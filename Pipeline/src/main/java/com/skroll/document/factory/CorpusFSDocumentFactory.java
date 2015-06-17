package com.skroll.document.factory;

import com.google.inject.BindingAnnotation;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;

/**
 * Use this annotation to get a CorpusFS based factory
 * Created by saurabh on 6/14/15.
 */
@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface CorpusFSDocumentFactory {
}
