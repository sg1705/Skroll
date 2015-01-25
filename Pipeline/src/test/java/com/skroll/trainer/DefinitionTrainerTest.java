package com.skroll.trainer;

import com.skroll.util.ObjectPersistUtil;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class DefinitionTrainerTest {


    @Test
    public void testGenerateFilesForOverride()  {
        String folderName = "src/main/resources/trainingDocuments/indentures/";

        try {
            DefinitionTrainer.generateHRFs(folderName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void testGenerateFileForOverride() {

        String fileName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";
        try {
            DefinitionTrainer.generateHRF(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void TestTrainWithOverride(){
        String fileName = "src/main/resources/trainingDocuments/indentures/Dish Indenture.html";
        try {
            DefinitionTrainer.trainWithOverride(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail(" failed to persist the model");
        }
    }
}