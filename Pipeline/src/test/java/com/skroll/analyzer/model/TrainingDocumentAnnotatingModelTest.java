package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.NaiveBayesWithFeatureConditions;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainingDocumentAnnotatingModelTest{
    String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining";
    TrainingDocumentAnnotatingModel model;




    public void testUpdateWithParagraph() throws Exception {

    }

    public void testUpdateTNBFWithParagraph() throws Exception {

    }

    public void testUpdateHMMWithParagraph() throws Exception {

    }

    @Test
    public void testUpdateWithDocument() throws Exception {


        model = new TrainingDocumentAnnotatingModel();

        System.out.println("initial model: \n" + model.getTnbfModel());

        File file = new File(trainingFolderName);
        if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();
            for (File f:listOfFiles) {
                Document doc = makeTrainingDoc(f);
                model.updateWithDocument(doc);
            }
        } else {
            Document doc = makeTrainingDoc(file);

            model.updateWithDocument(doc);
        }

        System.out.println("trained model: \n" + model);
    }

    @Test
    public void testGenerateDocumentFeatures() throws Exception {
        String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";
        File file = new File(trainingFolderName);


        TrainingDocumentAnnotatingModel model = new TrainingDocumentAnnotatingModel();
        Document doc = makeTrainingDoc(file);

        List<CoreMap> paragraphs = new ArrayList<>();
        for( CoreMap paragraph : doc.getParagraphs())
            paragraphs.add(DocumentAnnotatingHelper.processParagraph(paragraph, model.getHmm().size()));
        int[] docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(paragraphs, doc.getParagraphs(),
                model.getParaCategory(),
                DocumentAnnotatingModel.DEFAULT_DOCUMENT_FEATURES,
                DocumentAnnotatingModel.DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL);
        System.out.println(Arrays.toString(docFeatureValues));

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
            //create a pipeline

            Pipeline<Document, Document> pipeline =
                    new Pipeline.Builder()
                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                            .build();
            Document doc = pipeline.process(htmlDoc);
            return doc;
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

    public NaiveBayesWithFeatureConditions getTnbf() {
        return model.getTnbfModel();
    }

    public TrainingDocumentAnnotatingModel getModel() {
        return model;
    }
}