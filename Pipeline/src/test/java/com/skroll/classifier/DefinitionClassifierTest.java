package com.skroll.classifier;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.skroll.document.Document;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.fail;

public class DefinitionClassifierTest {

    @Test
     public void testTrainFolders() {

        try {
            Configuration configuration = new Configuration();
        } catch (IOException e) {
            e.printStackTrace();
            fail("failed to read configuration");
        }
        DefinitionClassifier documentClassifier = new DefinitionClassifier();
        String folderName = "src/main/resources/trainingDocuments/indentures";

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
                Document doc = Parser.parseDocumentFromHtml(htmlText);

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

    @Test
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
        Document doc = Parser.parseDocumentFromHtml(htmlText);

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
