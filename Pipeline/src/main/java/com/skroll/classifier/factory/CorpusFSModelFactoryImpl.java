package com.skroll.classifier.factory;

import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;

import javax.inject.Inject;

/**
 * Created by saurabhagarwal on 6/20/15.
 */
public class CorpusFSModelFactoryImpl extends
        FSModelFactoryImpl {

    @Inject
    public CorpusFSModelFactoryImpl(Configuration configuration) {
        modelFolderName = configuration.get("modelFolder", "/tmp");
        objectPersistUtil = new ObjectPersistUtil(modelFolderName);;
    }
}