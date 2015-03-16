package com.skroll.analyzer.model;

import com.skroll.analyzer.model.bn.TrainingNaiveBayes;
import com.skroll.analyzer.model.bn.TrainingNaiveBayesWithFeatureConditions;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainingDocumentAnnotatingModelTest extends TestCase {
    TrainingNaiveBayesWithFeatureConditions tnbf = new TrainingNaiveBayesWithFeatureConditions(
            RandomVariableType.PARAGRAPH_HAS_DEFINITION,
            DocumentAnnotatingModel.PARAGRAPH_FEATURES,
            DocumentAnnotatingModel.PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL,
            DocumentAnnotatingModel.DOCUMENT_FEATURES
    );
    public void testUpdateWithParagraph() throws Exception {

    }

    public void testUpdateTNBFWithParagraph() throws Exception {

    }

    public void testUpdateHMMWithParagraph() throws Exception {

    }

    public void testUpdateWithDocument() throws Exception {

        System.out.println("initial model: \n" + tnbf);

        //String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining";
        String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";
        TrainingDocumentAnnotatingModel model = new TrainingDocumentAnnotatingModel(tnbf);


        File file = new File(trainingFolderName);
        if (file.isDirectory()) {
            File[] listOfFiles = file.listFiles();
            for (File f:listOfFiles) {
                Document doc = makeDoc(file);
                //TrainingDocumentAnnotatingModel model = buildModel(f);
                model.updateWithDocument(doc);
            }
        } else {
            //TrainingDocumentAnnotatingModel model = buildModel(file);
            Document doc = makeDoc(file);

            model.updateWithDocument(doc);
        }

        System.out.println("trained model: \n" + tnbf);
    }

    public void testGenerateDocumentFeatures() throws Exception {
        String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";
        File file = new File(trainingFolderName);


        TrainingDocumentAnnotatingModel model = new TrainingDocumentAnnotatingModel();
        Document doc = makeDoc(file);

        List<CoreMap> paragraphs = new ArrayList<>();
        for( CoreMap paragraph : doc.getParagraphs())
            paragraphs.add(DocumentAnnotatingHelper.processParagraph(paragraph));
        int[] docFeatureValues = DocumentAnnotatingHelper.generateDocumentFeatures(paragraphs,
                DocumentAnnotatingModel.DOCUMENT_FEATURES,
                DocumentAnnotatingModel.PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL);
        System.out.println(Arrays.toString(docFeatureValues));

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
            //create a pipeline

            Pipeline<Document, Document> pipeline =
                    new Pipeline.Builder()
                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                            .build();
            Document doc = pipeline.process(htmlDoc);
            //return new TrainingDocumentAnnotatingModel(doc, tnbf);
            return doc;
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }
        return null;
    }
//
//    void processFile(File file, TrainingNaiveBayesWithFeatureConditions tnbf) {
//        String htmlString = null;
//        try {
//            htmlString = Utils.readStringFromFile(file);
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Error reading file");
//        }
//
//        try {
//            Document htmlDoc = new Document();
//            htmlDoc = Parser.parseDocumentFromHtml(htmlString);
//            //create a pipeline
//
//            Pipeline<Document, Document> pipeline =
//                    new Pipeline.Builder()
//                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
//                            .build();
//            Document doc = pipeline.process(htmlDoc);
//            TrainingDocumentAnnotatingModel docModel = new TrainingDocumentAnnotatingModel(doc, tnbf);
//
//            docModel.updateWithDocument();
//        } catch(Exception e) {
//            e.printStackTrace();
//            System.err.println("Error reading file");
//        }
//
//    }
}