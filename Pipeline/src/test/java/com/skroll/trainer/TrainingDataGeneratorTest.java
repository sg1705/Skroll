package com.skroll.trainer;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.fail;

public class TrainingDataGeneratorTest {


    @Test
    public void testGenerateFilesForOverwrite()  {
        String folderName = "src/main/resources/trainingDocuments/indentures/";

        try {
            TrainingDataGenerator.generateFilesForOverwrite(folderName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }

    @Test
    public void testGenerateFileForOverwrite() {

        String fileName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";

        try {
            TrainingDataGenerator.generateFileForOverwrite(fileName);
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to create overwrite files");
        }
    }
}