package com.skroll.document.factory;

import com.google.inject.BindingAnnotation;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use this annotation to get factory for benchmark files
 * Created by saurabh on 6/14/15.
 */
@Qualifier
@BindingAnnotation
@Retention(RUNTIME)
@Target({ TYPE, FIELD, PARAMETER, METHOD })
public @interface BenchmarkFSDocumentFactory {
}
