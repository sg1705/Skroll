package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.data.NBMNData;
import com.skroll.analyzer.model.bn.node.MultiplexNode;
import com.skroll.classifier.Category;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.PhantomJsExtractor;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class TrainingDocumentAnnotatingModelTest{
    int maxNumWords = 20;
    String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining/mini-indenture.html";
    //    String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html";
    //ModelRVSetting setting = new DefModelRVSetting(Category.DEFINITION,Category.DEFINITION_NAME,2);
    ModelRVSetting setting = new DefModelRVSetting(ClassifierFactory.CLASSIFIER_DEF, ClassifierFactory.CLASSIFIER_DEF_NAME,ClassifierFactory.DEF_CATEGORY_IDS.size());
    TrainingDocumentAnnotatingModel model = new TrainingDocumentAnnotatingModel();
    Document document;
    @Before
    public void setup() {
        File f = new File(trainingFolderName);
        document = makeTrainingDoc(f);
    }
    @Test
    public void testGetTrainingWeights() {
        for (CoreMap paragraph : document.getParagraphs()) {
            TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.DEFINITION, (float) 1.0);
            double[] trainingWeights = model.getTrainingWeights(paragraph);
            for (int i=0; i<trainingWeights.length; i++)
            System.out.println("paraId:" + paragraph.getId() + " TrainingWeight:" + trainingWeights[i]);
            assert(trainingWeights[1]==1.0);
        }
    }

    @Test
    public void testUpdateWithDocumentAndWeight() throws Exception {
                for (CoreMap paragraph : document.getParagraphs()) {
                    paragraph.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
                    paragraph.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
                    TrainingWeightAnnotationHelper.setTrainingWeight(paragraph, Category.DEFINITION, (float) 1.0);
                }
                model.updateWithDocumentAndWeight(document);

        System.out.println("trained model: \n" + model);
        assert(model.toString().contains("nextTokenCounts [Operations=5.0, Tiger=6.0]"));
//        assert(model.toString().contains("[WordNode{parameters=Operations=[0.0, 2.0] Tiger=[0.0, 1.0] Notwithstanding=[0.0, 2.0]"));
    }

    @Test
    public void testUpdateWithDocument() throws Exception {
        String trainingFolderName = "src/test/resources/analyzer/evaluate/docclassifier/AMC Networks CA.html";
        System.out.println("initial model: \n" + model.getNbmnModel());
        File f = new File(trainingFolderName);
//        document = makeTrainingDoc(f);
         model.updateWithDocument(document);


        System.out.println("trained model: \n" + model);
        assert(model.toString().contains("nextTokenCounts [Operations=5.0, Tiger=6.0]"));
//        assert(model.toString().contains("[WordNode{parameters=Operations=[2.0, 0.0] Tiger=[1.0, 0.0] Notwithstanding=[2.0, 0.0]"));
        MultiplexNode node = model.getNbmnModel().getMultiNodes().get(0);
        assert (Arrays.equals(node.getSelectingNode().getParameters(), new double[]{3.1, 1.1}));
        assert (Arrays.equals(node.getNodes()[0].getParameters(), new double[]{0.1, 0.1, 0.1, 3.1}));
        assert (Arrays.equals(node.getNodes()[1].getParameters(), new double[]{0.1, 0.1, 0.1, 1.1}));
        assert (Arrays.equals(node.getNodes()[0].getParents()[0].getParameters(), new double[]{0.1, 3.1}));
        assert (Arrays.equals(node.getNodes()[1].getParents()[0].getParameters(), new double[]{0.1, 1.1}));

    }


    @Test
    public void testGenerateDocumentFeatures() throws Exception {
        PhantomJsExtractor.TEST_FLAGS = true;
//        String trainingFolderName = "src/test/resources/analyzer/evaluate/docclassifier/AMC Networks CA.html";
        File file = new File(trainingFolderName);


        TrainingDocumentAnnotatingModel model = new TrainingDocumentAnnotatingModel();
        Document doc = makeTrainingDoc(file);

        List<CoreMap> processedParas = DocProcessor.processParas(doc, maxNumWords);
        NBMNData data = DocProcessor.getParaDataFromDoc(doc, processedParas, setting.getNbmnConfig());
        int[][] docFeatureValues = DocProcessor.generateDocumentFeatures(
                doc.getParagraphs(), data.getParaDocFeatures(), setting.getNbmnConfig());

        System.out.println(Arrays.deepToString(docFeatureValues));

        assert (Arrays.deepEquals(docFeatureValues, new int[][]{{1, 1}, {0, 1}, {0, 0}, {0, 0}, {0, 1}}));
    }
    Document makeTrainingDoc(File file){
        String htmlString = null;
        try {
            htmlString = Utils.readStringFromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }

        try {
            Document htmlDoc = new Document();
            htmlDoc = Parser.parseDocumentFromHtml(htmlString);
           /*
            for (CoreMap paragraph: htmlDoc.getParagraphs()) {
                CategoryAnnotationHelper.setMatchedText(paragraph, Lists.newArrayList(paragraph.getTokens().get(0)), Category.TOC_1);
            }
            */
            Pipeline<Document, Document> pipeline =
                    new Pipeline.Builder()
                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                            .build();
            Document doc = pipeline.process(htmlDoc);

            return htmlDoc;
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }
        return null;
    }
    Document makeDoc(File file){
        String htmlString = null;
        try {
            htmlString = Utils.readStringFromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }

        try {
            Document htmlDoc = new Document();
            htmlDoc = Parser.parseDocumentFromHtml(htmlString);

            return htmlDoc;
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }
        return null;
    }

    public TrainingDocumentAnnotatingModel getModel() {
        return model;
    }
}