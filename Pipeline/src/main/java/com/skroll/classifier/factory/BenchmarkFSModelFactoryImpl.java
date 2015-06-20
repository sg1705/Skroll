package com.skroll.classifier.factory;

import com.skroll.util.Configuration;

import javax.inject.Inject;

/**
 * Created by saurabhagarwal on 6/20/15.
 */
public class BenchmarkFSModelFactoryImpl extends
        FSModelFactoryImpl {

    @Inject
    public BenchmarkFSModelFactoryImpl(Configuration configuration) {
        modelFolderName = configuration.get("benchmarkModelFolder", "/tmp");
    }
}