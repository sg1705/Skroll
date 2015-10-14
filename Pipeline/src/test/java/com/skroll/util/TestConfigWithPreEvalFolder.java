package com.skroll.util;

import com.google.inject.BindingAnnotation;

import javax.inject.Inject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by saurabh on 6/15/15.
 */
public class TestConfigWithPreEvalFolder extends Configuration {

    String location;

    @Inject
    public TestConfigWithPreEvalFolder(@Location String location) {
        super("src/main/resources/skroll-trainer.properties");
        this.confMap.put("preEvaluatedFolder", location);
        logger.info("Configuration After overwriting the preEvaluatedFolder: " + this.confMap);
        this.location = location;
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @BindingAnnotation
    public @interface Location {
    }
}