package com.skroll.trainer;

import org.junit.Test;

import static org.junit.Assert.fail;

public class SkrollTrainerTest {


    @Test
    public void TestTrainWithWeight(){
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        String fileName = "build/resources/main/preEvaluated/";
        try {
            skrollTrainer.trainFolderUsingTrainingWeight(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }
    @Test
    public void TestBenchmark(){
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        try {
            System.out.println(skrollTrainer.runQCOnBenchmarkFile("d452134d10k.htm"));
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void TestQCOnBenchmarkFolder(){
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        try {
            System.out.println(skrollTrainer.runQCOnBenchmarkFolder());
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }
}