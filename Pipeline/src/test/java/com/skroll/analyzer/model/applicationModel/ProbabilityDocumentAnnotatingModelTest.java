package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.ParaInCategoryComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//todo: prior count is not set properly, making the probability favoring positive class.
public class ProbabilityDocumentAnnotatingModelTest {
    private static final List<Integer> TEST_DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.DEFINITION));
    private static final int TEST_DEF_CLASSIFIER_ID = 2;
    ModelRVSetting setting = new DefModelRVSetting(TEST_DEF_CATEGORY_IDS);

    TrainingDocumentAnnotatingModel tModel = new TrainingDocumentAnnotatingModel(TEST_DEF_CLASSIFIER_ID,setting);

    //    String testingFileName = "src/test/resources/classifier/smaller-indenture.html";
//    String testingFileName = "src/test/resources/analyzer/definedTermExtractionTesting/mini-indenture.html";
    String testingFileName = "src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html";
//    String testingFileName = "src/test/resources/analyzer/hmmTrainingDocs/AMD CA - Def No Quotes.html";


    File file = new File(testingFileName);
    Document doc = TestHelper.makeTrainingDoc(file);
//
//    Document doc = TestHelper.setUpTestDoc();

    ProbabilityDocumentAnnotatingModel model;
    boolean doneSetup=false;

    RandomVariable paraType = RVCreater.createDiscreteRVWithComputer(new ParaInCategoryComputer(Category.DEFINITION), "paraTypeIsCategory-" + Category.DEFINITION);


    @Before
    public void setup() throws Exception{
        if (doneSetup) return;
        doneSetup = true;

        tModel.updateWithDocument(doc);
        model = new ProbabilityDocumentAnnotatingModel(TEST_DEF_CLASSIFIER_ID, tModel.getNbmnModel(),
                tModel.getHmm(),
                tModel.getSecNbmnModel(),
                tModel.getHmm(),
                doc,
                new DefModelRVSetting(TEST_DEF_CATEGORY_IDS)
                );
        model.getHmm().updateProbabilities();
        System.out.println("HMM\n");
        System.out.println(model.getHmm());
    }

    @Test
    public void testInitialize() throws Exception {

        System.out.println(model);
        System.out.println("initial believes\n");
        printBelieves();
        double[][] pBelieves = model.getParagraphCategoryProbabilities();
        assert (Math.round(100 * pBelieves[51][1]) == 100);
    }

    @Test
    public void testDocBeliefs(){
        printDocBeliefs();
        for (int i=0; i<250;i++) {
            System.out.println("iteration " + i);
            model.passMessagesToParagraphCategories();
            model.passMessageToDocumentFeatures();
            printDocBeliefs();
        }
        System.out.println("After passing messages :\n");
        double[][][] dBelieves = model.getDocumentFeatureBelief();
        System.out.println(Arrays.deepToString(dBelieves[0]));
        assert (((int) (dBelieves[0][1][0]) == -24556));
        assert (((int) (dBelieves[0][1][1]) == 0));
    }

    void printDocBeliefs(){
        System.out.print("document level feature believes\n");
//        System.out.println(model.DEFAULT_DOCUMENT_FEATURES);
        System.out.print(" " + model.getNbmnConfig().getDocumentFeatureVarList());
        System.out.println();
        double[][][] dBelieves = model.getDocumentFeatureBelief();
        for (int i=0; i<dBelieves.length; i++){
            System.out.println(Arrays.deepToString(dBelieves[i]));
        }
    }

    void printBelieves(){
        System.out.print("document level feature believes\n");
//        System.out.println(model.DEFAULT_DOCUMENT_FEATURES);
        System.out.println(" " + model.getNbmnConfig().getDocumentFeatureVarList());
        double[][][] dBelieves = model.getDocumentFeatureProbabilities();
        for (int i=0; i<dBelieves.length; i++){
            System.out.println(Arrays.deepToString(dBelieves[i]));
        }

        List<CoreMap> paraList = doc.getParagraphs();

        System.out.print("document level feature believes\n");
        double[][] pBelieves = model.getParagraphCategoryProbabilities();

        for (int i=0; i<paraList.size(); i++){
         //   BNInference.normalizeLog(pBelieves[i]);

            System.out.print(i+" [");
            for (int j=0; j<pBelieves[i].length; j++)
                System.out.printf("%.2f ", pBelieves[i][j]);
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
        System.out.println(Arrays.toString(model.getParagraphCategoryProbabilities()[7]));
        assert (((int) (model.getParagraphCategoryProbabilities()[7][0] * 1000)) == 997);
        assert (((int) (model.getParagraphCategoryProbabilities()[7][1] * 1000)) == 2);
    }



    @Test
    public void testPassMessageToDocumentFeatures() throws Exception {
        System.out.println("After passing message to paragraphCategory once:\n");

        model.passMessagesToParagraphCategories();
        printBelieves();
        System.out.println("After passing message to documentFeatures once:\n");

        model.passMessageToDocumentFeatures();

        printBelieves();

        // strangely, gradlew test calculation results are different at lower decimal digits.
        assert (((int) (model.getDocumentFeatureBelief()[0][1][0] * 10)) == -1000);
        assert (((int) (model.getDocumentFeatureBelief()[0][1][1] * 10)) == -2);
    }


    @Test
    public void testPassMessages() throws Exception {

        model.passMessagesToParagraphCategories();

        model.passMessageToDocumentFeatures();
        model.passMessagesToParagraphCategories();

        model.passMessageToDocumentFeatures();
        model.passMessagesToParagraphCategories();
        System.out.println("After passing messages :\n");

        printBelieves();
    }
    public void testUpdateBelieves() throws Exception {

    }

    public void testNormalize() throws Exception {

    }

    public void testNormalizeParagraphBelieves() throws Exception {

    }

    @Test
    public void testAnnotateDocument() throws Exception {
        model.annotateParagraphs();

        System.out.println("annotated terms\n");
        RVValues.printAnnotatedDoc(doc);
        printBelieves();
    }

    public void testGetParagraphCategoryBelief() throws Exception {

    }

    public void testGetDocumentFeatureBelief() throws Exception {

    }


    @Test
    public void testUpdateBeliefWithObservation() throws Exception {
        model.passMessagesToParagraphCategories();
        model.passMessageToDocumentFeatures();
        model.passMessagesToParagraphCategories();


        model.annotateParagraphs();

        System.out.println("annotated terms\n");
        RVValues.printAnnotatedDoc(doc);
//        DocumentAnnotatingHelper.printAnnotatedDoc(doc);
    }

}