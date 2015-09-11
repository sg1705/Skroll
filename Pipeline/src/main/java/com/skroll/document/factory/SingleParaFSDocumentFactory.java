package com.skroll.document.factory;

import com.google.inject.BindingAnnotation;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use this annotation to get a SingleParaFSDocumentFactory
 * Created by saurabhagarwal on 8/30/2015.
 */
@Qualifier
@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface SingleParaFSDocumentFactory {
}
