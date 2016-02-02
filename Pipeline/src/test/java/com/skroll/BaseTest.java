package com.skroll;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.parser.Parser;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;

/**
 * Created by saurabh on 1/31/16.
 */
public abstract class BaseTest {

    protected Injector injector;
    protected Parser  parser;

    @Before
    public void setupBase() {
        try {
            injector = Guice.createInjector(new SkrollTestGuiceModule());
            parser = injector.getInstance(Parser.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
