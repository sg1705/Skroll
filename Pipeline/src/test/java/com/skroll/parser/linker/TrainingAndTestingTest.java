package com.skroll.parser.linker;

import com.skroll.analyzer.train.Trainer;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.io.File;
import java.util.HashSet;

public class TrainingAndTestingTest {

    public static void trainNB() {

        String[] trainingFolder = {
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/not-pdef-words",
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/pdef-words"};

        Trainer.trainBinaryNaiveBayes(trainingFolder[0], Constants.CATEGORY_NEGATIVE);
        Trainer.trainBinaryNaiveBayes(trainingFolder[1], Constants.CATEGORY_POSITIVE);

    }

    public static void trainHMM() {
        String trainingFolder = "src/test/resources/analyzer/hmmTrainingDocs";
        Trainer.trainHiddenMarkovModel(trainingFolder);
    }

}