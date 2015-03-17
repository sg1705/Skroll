package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.document.Document;
import junit.framework.TestCase;

import java.io.File;

public class ProbabilityDocumentAnnotatingModelTest extends TestCase {
    String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";
    File file = new File(trainingFolderName);
    TrainingDocumentAnnotatingModelTest traingTest = new TrainingDocumentAnnotatingModelTest();
    Document doc = traingTest.makeDoc(file);
    ProbabilityDocumentAnnotatingModel model;


    public void test() throws  Exception{
    }

    public void testInitialize() throws Exception {
        traingTest.testUpdateWithDocument();
        model= new ProbabilityDocumentAnnotatingModel( traingTest.getTnbf(), doc);
        System.out.println(model);

    }

    public void testComputeInitalBelieves() throws Exception {

    }

    public void testPassMessagesToParagraphCategories() throws Exception {

    }

    public void testPassMessageToDocumentFeatures() throws Exception {

    }

    public void testUpdateBelieves() throws Exception {

    }

    public void testNormalize() throws Exception {

    }

    public void testNormalizeParagraphBelieves() throws Exception {

    }

    public void testAnnotateDocument() throws Exception {

    }

    public void testGetParagraphCategoryBelief() throws Exception {

    }

    public void testGetDocumentFeatureBelief() throws Exception {

    }
}