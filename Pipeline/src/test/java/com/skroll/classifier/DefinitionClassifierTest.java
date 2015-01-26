package com.skroll.classifier;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public class DefinitionClassifierTest {

    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(DefinitionClassifierTest.class);
    @Test
    public void testTrainClassify() {

        Classifier documentClassifier = new DefinitionClassifier();
        //convertRawToProcessedCorpus(rawFolder, ProcessedFolder);
        try {
            testTrainFolders("src/test/resources/analyzer/hmmTrainingDocs");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("failed training");
        }
        String testingFile = "src/test/resources/parser/linker/test-linker-random.html";
        try {
            Document document = (Document)documentClassifier.classify(Parser.parseDocumentFromHtmlFile(testingFile));
            Utils.writeToFile("build/classes/test/test-linker-random.html", document.getTarget());
        } catch(Exception ex){
            fail("failed testClassify");
        }
    }

    @Test
    public void testClassify() {

        Classifier documentClassifier = new DefinitionClassifier();
        // String testingFile = "src/test/resources/parser/linker/test-linker-random.html";
        String testingFile = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";

        Document document = null;
        try {
            document = (Document)documentClassifier.classify(Parser.parseDocumentFromHtmlFile(testingFile));
        } catch (ParserException e) {
            e.printStackTrace();
            fail("failed to parse document");
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to find a Model");
        }
        logger.debug ("Number fo Paragraphs returned: " + document.getParagraphs().size());
            Utils.writeToFile("build/classes/test/test-linker-random.html", document.getTarget());

    }

    //@Test
     public void testTrainFolders(String folderName) {

        try {
            Configuration configuration = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
            fail("failed to read configuration");
        }
        DefinitionClassifier documentClassifier = new DefinitionClassifier();
        //String folderName = "src/main/resources/trainingDocuments/indentures";

        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(folderName));
        for (File f : iterable) {
            if (f.isFile()) {
                    String fileName = f.getPath();
                String htmlText = null;
                try {
                    htmlText = Utils.readStringFromFile(fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                    fail("failed to read the file");
                }
                //parse the file into document
                Document doc = null;
                try {
                    doc = Parser.parseDocumentFromHtml(htmlText);
                } catch (ParserException e) {
                    e.printStackTrace();
                    fail("failed to parse the file");
                }

                // extract the definition from paragraph from html doc.
                Pipeline<Document, Document> pipeline =
                        new Pipeline.Builder()
                                .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                                .build();
                doc = pipeline.process(doc);
                documentClassifier.train(doc);

            }
        }
        try {
            documentClassifier.persistModel();
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed to persist the model");
        }
    }

    //@Test
    public void testTrainFile()  {

        try {
            Configuration configuration = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
            fail(" failed to read configuration");
        }
        DefinitionClassifier documentClassifier = new DefinitionClassifier();
        String fileName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";

        String htmlText = null;
        try {
            htmlText = Utils.readStringFromFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fail("failed to read file");
        }
        //parse the file into document
        Document doc = null;
        try {
            doc = Parser.parseDocumentFromHtml(htmlText);
        } catch (ParserException e) {
            e.printStackTrace();
            fail("failed to parse the file");
        }

        // extract the definition from paragraph from html doc.
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .build();
        doc = pipeline.process(doc);
        documentClassifier.train(doc);
        try {
            documentClassifier.persistModel();
        } catch (ObjectPersistUtil.ObjectPersistException e) {
            e.printStackTrace();
            fail("failed to persist the model");
        }
    }
}
