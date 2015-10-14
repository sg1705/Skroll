package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.data.NBMNData;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.parser.extractor.PhantomJsExtractor;
import com.skroll.parser.extractor.TestMode;
import com.skroll.util.TestHelper;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainingDocumentAnnotatingModelTest{
    int maxNumWords = 20;
    String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining/mini-indenture.html";
    //    String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html";

    private static final List<Integer> TEST_DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.DEFINITION));
    private static final int TEST_DEF_CLASSIFIER_ID = 2;
    ModelRVSetting setting = new DefModelRVSetting(TEST_DEF_CATEGORY_IDS);
    TrainingTextAnnotatingModel model = new TrainingTextAnnotatingModel(TEST_DEF_CLASSIFIER_ID, setting);
    Document document;
    @Before
    public void setup() throws Exception {
        File f = new File(trainingFolderName);
        document = TestHelper.setUpTestDoc();
    }
    @Test
    public void testGetTrainingWeights() {
        // training weight already set in test doc

        // output weight for inspection
        for (CoreMap paragraph : document.getParagraphs()) {
            double[] trainingWeights = model.getTrainingWeights(paragraph);
            for (int i=0; i<trainingWeights.length; i++)
                System.out.println("paraId:" + paragraph.getId() + " TrainingWeight:" + trainingWeights[i]);
        }
        CoreMap para0 = document.getParagraphs().get(0);
        assert (model.getTrainingWeights(para0)[1] == 1.0); // weight of definition class for para0 is 1.
    }

    @Test
    public void testUpdateWithDocumentAndWeight() throws Exception {

        model.updateWithDocumentAndWeight(document);

        System.out.println("trained model: \n" + model);
        assert (model.toString().contains("in=[0.0, 0.02857142857142857]"));
    }

    @Test
    public void testUpdateWithDocument() throws Exception {
        String trainingFolderName = "src/test/resources/analyzer/evaluate/docclassifier/AMC Networks CA.html";
        System.out.println("initial model: \n" + model.getNbmnModel());
        File f = new File(trainingFolderName);
        document = TestHelper.makeTrainingDoc(f);
        model.updateWithDocument(document);
        model.getHmm().updateProbabilities();
        System.out.println("trained model: \n" + model);
        System.out.println("multiNodes: \n" + model.getNbmnModel().getMultiNodes());
        assert (model.getNbmnModel().getMultiNodes().toString().contains(
                "RandomVariable{name='[0, 1]_1_notInTable', featureSize=2, valueNames=null}], parameters=[1.0E-4, 1.0E-4, 1.0E-4, 307.0001]}]}"
        ));

    }


    @Test
    public void testGenerateDocumentFeatures() throws Exception {
        PhantomJsExtractor.TEST_MODE = TestMode.ON;
//        String trainingFolderName = "src/test/resources/analyzer/evaluate/docclassifier/AMC Networks CA.html";
//        File file = new File(trainingFolderName);

        ModelRVSetting setting = new DefModelRVSetting(TEST_DEF_CATEGORY_IDS);

        TrainingTextAnnotatingModel model = new TrainingTextAnnotatingModel(TEST_DEF_CLASSIFIER_ID, setting);
        //Document doc = makeTrainingDoc(file);

        List<CoreMap> processedParas = DocProcessor.processParas(document);
        NBMNData data = DocProcessor.getParaDataFromDoc(document, setting.getNbmnConfig());
        int[][] docFeatureValues = DocProcessor.generateDocumentFeatures(
                document.getParagraphs(), data.getParaDocFeatures(), setting.getNbmnConfig());

        System.out.println(Arrays.deepToString(docFeatureValues));
        //TODO: need to verify whether these values are correct
        assert (Arrays.deepEquals(docFeatureValues, new int[][]{{0, 0}, {0, 1}, {0, 0}, {0, 0}, {0, 0}}));
    }

    public TrainingTextAnnotatingModel getModel() {
        return model;
    }
}