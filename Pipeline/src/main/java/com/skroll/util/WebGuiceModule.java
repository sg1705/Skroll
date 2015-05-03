package com.skroll.util;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.servlet.ServletModule;
import com.squarespace.jersey2.guice.JerseyGuiceServletContextListener;

import java.util.Arrays;
import java.util.List;

/**
 * Created by saurabh on 4/17/15.
 */
public class WebGuiceModule extends JerseyGuiceServletContextListener {
    @Override
    protected List<? extends Module> modules() {

        // Guice Module
        AbstractModule module = new SkrollGuiceModule();

        return Arrays.asList(module, new ServletModule());
    }
}
