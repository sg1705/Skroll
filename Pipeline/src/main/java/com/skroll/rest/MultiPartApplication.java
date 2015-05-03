package com.skroll.rest;

import com.skroll.util.WebGuiceModule;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by saurabhagarwal on 1/31/15.
 * This class is required to add the multipart feature to jersey servlet.
 */
public class MultiPartApplication extends Application{
    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<Class<?>>();
        // register resources and features
        classes.add(MultiPartFeature.class);
        classes.add(DocAPI.class);
        classes.add(InstrumentAPI.class);
        classes.add(WebGuiceModule.class);
        return classes;
    }
}
