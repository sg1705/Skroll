package com.skroll.trainer;

import com.google.inject.BindingAnnotation;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * TrainerConfiguration is the configuration used for command line training utility.
 * Created by saurabh on 6/15/15.
 */
public class TrainerConfiguration extends Configuration {

    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(Configuration.class);

    String location;
    @Inject
    public TrainerConfiguration(@Location String location) {
        super("skroll.properties");
        this.confMap.put("preEvaluatedFolder",location);
        logger.info("Configuration After overwriting the preEvaluatedFolder: " + this.confMap);
        this.location = location;
        }

        @Retention(RetentionPolicy.RUNTIME)
        @Target({ElementType.FIELD, ElementType.PARAMETER})
        @BindingAnnotation
        public @interface Location {}
}
