package com.skroll.classifier;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.BaseTest;
import com.skroll.document.Document;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;
import com.skroll.util.SkrollTestGuiceModule;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

import static org.junit.Assert.fail;

public class ClassifierImplTest extends BaseTest {

    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(ClassifierImplTest.class);
    private static ClassifierFactory classifierFactory = new ClassifierFactory();
    private static Classifier documentClassifier = null;
    private static Classifier tocClassifier = null;
    private Configuration config;

    @Before
    public void setup(){
      try {
          Injector injector = Guice.createInjector(new SkrollTestGuiceModule());
          config = injector.getInstance(Configuration.class);
          ClassifierFactory classifierFactory = injector.getInstance(ClassifierFactory.class);
          documentClassifier = classifierFactory.getClassifier(ClassifierFactory.UNIVERSAL_TOC_CLASSIFIER_ID);
          tocClassifier = classifierFactory.getClassifier(ClassifierFactory.UNIVERSAL_TOC_CLASSIFIER_ID);
     } catch (Exception e) {
        e.printStackTrace();
        }
    }


    @Test
    public void testClassify() {

        String testingFile = "src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html";
        Document document = null;
        try {
            document = (Document)documentClassifier.classify(testingFile,parser.parseDocumentFromHtmlFile(testingFile));
            document.setId("1");
        } catch (ParserException e) {
            e.printStackTrace();
            fail("failed to parse document");
        } catch (Exception e) {
            e.printStackTrace();
            fail(" failed to find a Model");
        }
        logger.debug ("Number fo Paragraphs returned: " + CategoryAnnotationHelper.getParagraphsAnnotatedWithCategory(document, Category.DEFINITION).size());
    }

    //@Test
     public void testTrainFolders(String folderName) {
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
                    doc = parser.parseDocumentFromHtml(htmlText);
                    doc.setId("");
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
    }

}
