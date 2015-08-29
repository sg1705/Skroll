package com.skroll.trainer;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.fail;

public class CategoryTrainerTest {

    public static final Logger logger = LoggerFactory.getLogger(CategoryTrainerTest.class);

    @Test
    public void TestTrainWithWeight(){
        CategoryTrainer categoryTrainer = new CategoryTrainer();
        try {
            categoryTrainer.trainFolderUsingTrainingWeight();
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }


    public void checkPreEvaluatedFile() {
        CategoryTrainer categoryTrainer = new CategoryTrainer();
        categoryTrainer.checkPreEvaluatedFile();
    }
}