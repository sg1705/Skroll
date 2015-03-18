package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ProbabilityDocumentAnnotatingModelTest {
    String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";
    File file = new File(trainingFolderName);
    TrainingDocumentAnnotatingModelTest traingTest = new TrainingDocumentAnnotatingModelTest();
    Document doc = traingTest.makeDoc(file);
    ProbabilityDocumentAnnotatingModel model;


    public void test() throws  Exception{
    }

    @Test
    public void testInitialize() throws Exception {
        traingTest.testUpdateWithDocument();
        model= new ProbabilityDocumentAnnotatingModel( traingTest.getTnbf(), doc);
        System.out.println(model);


        System.out.println("initial believes\n");
        printBelieves();

    }

    void printBelieves(){
        System.out.print("document level feature believes\n");
        double[][] dBelieves = model.getDocumentFeatureBelief();
        for (int i=0; i<dBelieves.length; i++){
            System.out.println(model.DOCUMENT_FEATURES);
            System.out.println(Arrays.toString(dBelieves[i]));
        }

        List<CoreMap> paraList = doc.getParagraphs();

        System.out.print("document level feature believes\n");
        double[][] pBelieves = model.getParagraphCategoryBelief();

        for (int i=0; i<paraList.size(); i++){
            System.out.println(paraList.get(i).getText());
            System.out.println(Arrays.toString(pBelieves[i]));
        }
    }

    public void testComputeInitalBelieves() throws Exception {

    }

    @Test
    public void testPassMessagesToParagraphCategories() throws Exception {
        traingTest.testUpdateWithDocument();
        model= new ProbabilityDocumentAnnotatingModel( traingTest.getTnbf(), doc);
        model.passMessagesToParagraphCategories();
        System.out.println("After passing message to paragraphCategory once:\n");
        printBelieves();
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