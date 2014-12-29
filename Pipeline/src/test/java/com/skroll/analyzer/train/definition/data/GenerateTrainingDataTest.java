package com.skroll.analyzer.train.definition.data;

import com.skroll.analyzer.train.definition.data.GenerateTrainingData;
import junit.framework.TestCase;

public class GenerateTrainingDataTest extends TestCase {

    public void testSetupDirectories() throws Exception {
        GenerateTrainingData generateTrainingData = new GenerateTrainingData();
        generateTrainingData.setupDirectories();
    }


}