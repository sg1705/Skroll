package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//todo: prior count is not set properly, making the probability favoring positive class.
public class ProbabilityDocumentAnnotatingModelTest {

//    String testingFileName = "src/test/resources/classifier/mini-indenture.html";

    //String testingFileName = "src/test/resources/classifier/smaller-indenture.html";
    String testingFileName = "src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html";
//    String testingFileName = "src/test/resources/analyzer/definedTermExtractionTesting/AMD CA - Def No Quotes.html";



    File file = new File(testingFileName);

    TrainingDocumentAnnotatingModelTest traingTest = new TrainingDocumentAnnotatingModelTest();

    Document doc = traingTest.makeDoc(file);

    ProbabilityDocumentAnnotatingModel model;
    boolean doneSetup=false;

    RandomVariableType wordType = RandomVariableType.WORD_IS_DEFINED_TERM;
    RandomVariableType paraType = RandomVariableType.PARAGRAPH_HAS_DEFINITION;
    List<RandomVariableType> wordFeatures = model.DEFAULT_WORD_FEATURES;
    List<RandomVariableType> paraFeatures = model.DEFAULT_PARAGRAPH_FEATURES;
    List<RandomVariableType> paraDocFeatures = model.DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL;
    List<RandomVariableType> docFeatures = model.DEFAULT_DOCUMENT_FEATURES;


    @Before
    public void setupOnce() throws Exception{
        if (doneSetup) return;
        doneSetup = true;

        traingTest.testUpdateWithDocument();
        model= new ProbabilityDocumentAnnotatingModel( traingTest.getTnbf(), traingTest.getModel().getHmm(), doc,
                wordType, wordFeatures, paraType, paraFeatures, paraDocFeatures, docFeatures
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

    }

    @Test
    public void testDocBeliefs(){
        printDocBeliefs();
        for (int i=0; i<250;i++) {
            model.passMessagesToParagraphCategories();
            model.passMessageToDocumentFeatures();
            System.out.println("After passing messages :\n");

            printDocBeliefs();
        }
    }

    void printDocBeliefs(){
        System.out.print("document level feature believes\n");
        System.out.println(model.DEFAULT_DOCUMENT_FEATURES);

        double[][] dBelieves = model.getDocumentFeatureBelief();
        for (int i=0; i<dBelieves.length; i++){
            System.out.println(Arrays.toString(dBelieves[i]));
        }
    }

    void printBelieves(){
        System.out.print("document level feature believes\n");
        System.out.println(model.DEFAULT_DOCUMENT_FEATURES);

        double[][] dBelieves = model.getDocumentFeatureProbabilities();
        for (int i=0; i<dBelieves.length; i++){
            System.out.println(Arrays.toString(dBelieves[i]));
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
    }



    @Test
    public void testPassMessageToDocumentFeatures() throws Exception {
        System.out.println("After passing message to paragraphCategory once:\n");

        model.passMessagesToParagraphCategories();
        printBelieves();
        System.out.println("After passing message to documentFeatures once:\n");

        model.passMessageToDocumentFeatures();

        printBelieves();
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
        model.annotateDocument();

        System.out.println("annotated terms\n");
        DocumentAnnotatingHelper.printAnnotatedDoc(doc);
        printBelieves();
    }

    public void testGetParagraphCategoryBelief() throws Exception {

    }

    public void testGetDocumentFeatureBelief() throws Exception {

    }

    @Test
    public void testUpdateBeliefWithZeroObservations() throws Exception {

        List<CoreMap> paraList = doc.getParagraphs();
        List<Token> definedTerms = new ArrayList<>();
        List<CoreMap> observedParas = new ArrayList<>();


        System.out.print("document level feature believes\n");
//        for (int i=0; i<2; i++){
//            paraList.get(i).set(CoreAnnotations.IsDefinitionAnnotation.class, true);
//            observedParas.add(paraList.get(i));
//            paraList.get(i).set(CoreAnnotations.IsUserObservationAnnotation.class, true);
//
//        }
        for (int i=0; i<0; i++){
            //paraList.get(i).set(CoreAnnotations.IsDefinitionAnnotation.class, false);
            DocumentAnnotatingHelper.clearParagraphCateoryAnnotation(paraList.get(i), paraType);

            observedParas.add(paraList.get(i));
            paraList.get(i).set(CoreAnnotations.IsUserObservationAnnotation.class, true);

        }

//        DocumentAnnotatingHelper.addParagraphTermAnnotation(paraList.get(1), paraType, Arrays.asList(paraList.get(1).getTokens().get(0)));
//        observedParas.add(paraList.get(1));
//        paraList.get(1).set(CoreAnnotations.IsUserObservationAnnotation.class, true);

        for (int p=paraList.size()-1; p>paraList.size()-4;p--) {

            DocumentAnnotatingHelper.addParagraphTermAnnotation(paraList.get(p), paraType, Arrays.asList(paraList.get(p).getTokens().get(0)));
            observedParas.add(paraList.get(p));
            paraList.get(p).set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        }
        model.updateBeliefWithObservation(observedParas);

        testDocBeliefs();

//        for (int i=0;i<5;i++) {
//            model.passMessagesToParagraphCategories();
//            model.passMessageToDocumentFeatures();
//        }
        model.annotateDocument();


        System.out.println("annotated terms\n");
        DocumentAnnotatingHelper.printAnnotatedDoc(doc);
        System.out.println("believes\n");

        printBelieves();

    }

    @Test
    public void testUpdateBeliefWithObservation() throws Exception {
        model.passMessagesToParagraphCategories();
        model.passMessageToDocumentFeatures();
        model.passMessagesToParagraphCategories();



        model.annotateDocument();

        System.out.println("annotated terms\n");
        DocumentAnnotatingHelper.printAnnotatedDoc(doc);
    }
}