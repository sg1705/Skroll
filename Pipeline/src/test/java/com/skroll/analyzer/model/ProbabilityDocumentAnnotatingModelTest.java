package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

//todo: prior count is not set properly, making the probability favoring positive class.
public class ProbabilityDocumentAnnotatingModelTest {
    String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html";
    File file = new File(trainingFolderName);
    TrainingDocumentAnnotatingModelTest traingTest = new TrainingDocumentAnnotatingModelTest();
    Document doc = traingTest.makeDoc(file);
    ProbabilityDocumentAnnotatingModel model;
    boolean doneSetup=false;


    @Before
    public void setupOnce() throws Exception{
        if (doneSetup) return;
        traingTest.testUpdateWithDocument();
        model= new ProbabilityDocumentAnnotatingModel( traingTest.getTnbf(), traingTest.getModel().getHmm(), doc);
        doneSetup = true;
    }

    @Test
    public void testInitialize() throws Exception {

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
            BNInference.normalizeLog(pBelieves[i]);

            System.out.print(i+" [");
            for (int j=0; j<pBelieves[i].length; j++)
                System.out.printf("%.0f ", pBelieves[i][j]);
            System.out.print("] ");
            System.out.println(paraList.get(i).getText());

        }
    }

    public void testComputeInitalBelieves() throws Exception {

    }

    @Test
    public void testPassMessagesToParagraphCategories() throws Exception {
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

    @Test
    public void testAnnotateDocument() throws Exception {
        model.annotateDocument();
        System.out.println("annotated terms\n");
        DocumentAnnotatingHelper.printAnnotatedDoc(doc);
    }

    public void testGetParagraphCategoryBelief() throws Exception {

    }

    public void testGetDocumentFeatureBelief() throws Exception {

    }

    @Test
    public void testAnnotateDocument1() throws Exception {
        model.annotateDocument();

    }
}