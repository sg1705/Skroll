package com.skroll.classifier;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.skroll.analyzer.model.TrainingDocumentAnnotatingModel;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.NoSuchFileException;

import static org.junit.Assert.fail;

public class DefinitionClassifierTest {

    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(DefinitionClassifierTest.class);
    Classifier documentClassifier = new DefinitionClassifier();
//    @Before
//    public void setup(){
//        Configuration configuration = new Configuration();
//        String modelFolderName = configuration.get("modelFolder","/tmp");
//        String path =modelFolderName + ((DefinitionClassifier)documentClassifier).getDtemModelName();
//        File file = new File(path);
//        file.delete();
//    }

    @Test
    public void testTrainClassify() {


        //convertRawToProcessedCorpus(rawFolder, ProcessedFolder);
        try {
            testTrainFolders("src/test/resources/analyzer/hmmTrainingDocs");
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("failed training");
        }
        String testingFile = "src/test/resources/parser/linker/test-linker-random.html";
//        try {
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//            String jsonSting = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(((DefinitionClassifier)documentClassifier).getModel());
//            TrainingDocumentAnnotatingModel model = mapper.readValue(jsonSting, TrainingDocumentAnnotatingModel.class);
//            Document document = (Document)documentClassifier.classify(Parser.parseDocumentFromHtmlFile(testingFile));
//            Utils.writeToFile("build/classes/test/test-linker-random.html", document.getTarget());
//        } catch(Exception ex){
//            ex.printStackTrace();
//            fail(ex.getMessage());
//        }
    }

    @Test
    public void testClassify() {

        String testingFile = "src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html";
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

        assert(DocumentHelper.getDefinitionParagraphs(document).size()==174);
        assert(DocumentHelper.getDefinedTermTokensInParagraph(
                DocumentHelper.getDefinitionParagraphs(document).get(172))
                .get(1).get(1).toString().equals("trustee"));

        logger.debug ("Number fo Paragraphs returned: " + document.getParagraphs().size());
            Utils.writeToFile("build/classes/test/test-linker-random.html", document.getTarget());

    }

    //@Test
     public void testTrainFolders(String folderName) {
            Configuration configuration = new Configuration();

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
//        try {
//            documentClassifier.persistModel();
//        } catch (ObjectPersistUtil.ObjectPersistException e) {
//            e.printStackTrace();
//            fail("failed to persist the model");
//        }
    }

    //@Test
    public void testTrainFile()  {
            Configuration configuration = new Configuration();

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
