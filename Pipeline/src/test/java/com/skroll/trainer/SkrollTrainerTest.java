package com.skroll.trainer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class SkrollTrainerTest {

    public static final Logger logger = LoggerFactory.getLogger(SkrollTrainerTest.class);

    @Test
    public void TestTrainWithWeight(){
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        String fileName = "src/test/resources/document/documentFactory/";
        try {
            skrollTrainer.trainFolderUsingTrainingWeight(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }


    public void checkPreEvaluatedFile() {
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        skrollTrainer.checkPreEvaluatedFile();
    }
}