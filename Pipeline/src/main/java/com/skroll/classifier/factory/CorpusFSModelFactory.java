package com.skroll.classifier.factory;

import com.google.inject.BindingAnnotation;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Created by saurabhagarwal on 6/20/15.
 */

@Qualifier
@BindingAnnotation
@Retention(RUNTIME)
@Target({ TYPE, FIELD, PARAMETER, METHOD })
public @interface CorpusFSModelFactory {

}
