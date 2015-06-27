package com.skroll.trainer;

import org.junit.Test;

import static org.junit.Assert.fail;

public class SkrollTrainerTest {


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
}