package com.skroll.trainer;

import com.skroll.util.ObjectPersistUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class SkrollTrainerTest {


    @Test
    public void testGenerateFilesForOverride()  {
        String folderName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        try {
            skrollTrainer.generateHRFs(folderName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void testGenerateFileForOverride() {
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        String fileName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";
        try {
            skrollTrainer.generateHRF(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void TestTrainWithOverride(){
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        String fileName = "src/main/resources/trainingDocuments/indentures/";
        try {
            skrollTrainer.trainWithOverride(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail(" failed to persist the model");
        }
    }

    @Test
    public void TestTrainWithWeight(){
        SkrollTrainer skrollTrainer = new SkrollTrainer();
        String fileName = "build/resources/main/preEvaluated/";
        try {
            skrollTrainer.trainFolderUsingTrainingWeight(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail(" failed to persist the model");
        }
    }
}