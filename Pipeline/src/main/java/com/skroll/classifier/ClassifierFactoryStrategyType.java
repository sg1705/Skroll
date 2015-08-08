package com.skroll.classifier;

import com.google.inject.BindingAnnotation;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Use this annotation to get a CorpusFS based factory
 * Created by saurabh on 6/14/15.
 */
@Qualifier
@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD }) @Retention(RUNTIME)
public @interface ClassifierFactoryStrategyType {
}
