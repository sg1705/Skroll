package com.skroll.trainer;

import com.skroll.util.ObjectPersistUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class DefinitionTrainerTest {


    @Test
    public void testGenerateFilesForOverride()  {
        String folderName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";

        try {
            ExperiementTrainer.generateHRFs(folderName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void testGenerateFileForOverride() {

        String fileName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";
        try {
            ExperiementTrainer.generateHRF(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void TestTrainWithOverride(){
        String fileName = "src/main/resources/trainingDocuments/indentures/";
        try {
            ExperiementTrainer.trainWithOverride(fileName);
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
        String fileName = "build/resources/main/preEvaluated/";
        try {
            ExperiementTrainer.trainFolderUsingTrainingWeight(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail(" failed to persist the model");
        }
    }
}