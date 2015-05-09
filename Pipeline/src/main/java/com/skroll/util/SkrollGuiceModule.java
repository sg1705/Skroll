package com.skroll.util;

import com.google.inject.AbstractModule;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.DocumentFactory;
import com.skroll.classifier.ModelFactory;

/**
 * Created by saurabhagarwal on 4/26/15.
 */
public class SkrollGuiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DocumentFactory.class);
        bind(ModelFactory.class);
        bind(ClassifierFactory.class);
    }
}