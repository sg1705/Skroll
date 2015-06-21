package com.skroll.document.factory;

import com.skroll.util.Configuration;

import javax.inject.Inject;

/**
 * Created by saurabh on 6/14/15.
 */
public class CorpusFSDocumentFactoryImpl extends
        FileSystemDocumentFactoryImpl {

    @Inject
    public CorpusFSDocumentFactoryImpl(Configuration configuration) {
        this.configuration = configuration;
        this.folder = configuration.get("preEvaluatedFolder");
        logger.info(folder);
    }

}
