package com.skroll.analyzer.model;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class DefinedTermExtractionModelTest extends TestCase {
    enum FileProcess{
        TRAIN,ANNOTATE
    }

    public void testAnnotateDefinedTermsInParagraph() throws Exception {
        DefinedTermExtractionModel model = new DefinedTermExtractionModel();

        String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining";
        trainingTest(trainingFolderName, model);
        System.out.println("DefinitionExtractionModel: \n" + model);

        String testingFolderName = "src/test/resources/analyzer/definedTermExtractionTesting";
        List<Document> docs = processFolder(testingFolderName, model, FileProcess.ANNOTATE);

        assert(DocumentHelper.getDefinitionParagraphs(docs.get(0)).size()==174);
        assert(DocumentHelper.getDefinedTermTokensInParagraph(
                DocumentHelper.getDefinitionParagraphs(docs.get(0)).get(172))
                .get(1).get(1).toString().equals("trustee"));

    }

    public void testUpdateWithDocument() throws Exception {
        String folderName = "src/test/resources/analyzer/definedTermExtractionTraining";
        DefinedTermExtractionModel model = new DefinedTermExtractionModel();
        trainingTest(folderName, model);
        System.out.println("DefinitionExtractionModel: \n" + model);
    }

    public void trainingTest(String folderName, DefinedTermExtractionModel model){
        processFolder(folderName, model,FileProcess.TRAIN);
        model.compile();
    }

    private List<Document> processFolder(String folderName, DefinedTermExtractionModel model, FileProcess action){
        File folder = new File(folderName);
        List<Document> docs= new ArrayList<>();

        if (folder.isDirectory()) {
            File[] listOfFiles = folder.listFiles();
            for (File file:listOfFiles) {
                docs.add( processFile(file, model,action) );
            }
        } else {
            docs.add( processFile(folder, model, action));
        }
        return docs;
    }

    private Document processFile(File file, DefinedTermExtractionModel model, FileProcess action) {
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
            switch (action){
                case TRAIN:
                    Pipeline<Document, Document> pipeline =
                            new Pipeline.Builder()
                                    .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)

                                    .build();
                    Document doc = pipeline.process(htmlDoc);
                    model.updateWithDocument(doc);
                    return null;

                case ANNOTATE:
                    // output before annotate:
                    System.out.println("definitions before annotate");
                    for (CoreMap para:DocumentHelper.getDefinitionParagraphs(htmlDoc)){
                        System.out.println(para.getText());
                        System.out.println(DocumentHelper.getDefinedTermTokensInParagraph(para));
                    }

                    model.annotateDefinedTermsInDocument(htmlDoc);

                    System.out.println("definitions after annotate");
                    for (CoreMap para:DocumentHelper.getDefinitionParagraphs(htmlDoc)){
                        System.out.println(para.getText());
                        System.out.println(DocumentHelper.getDefinedTermTokensInParagraph(para));
                    }
                    System.out.println(DocumentHelper.getDefinitionParagraphs(htmlDoc).size());

                    return htmlDoc;

            }
        } catch(Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }
        return null;
    }
}