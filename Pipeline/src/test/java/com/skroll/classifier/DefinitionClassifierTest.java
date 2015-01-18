package com.skroll.classifier;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.fail;

public class DefinitionClassifierTest {

    @Test
     public void testTrain() throws ObjectPersistUtil.ObjectPersistException, IOException {

        Configuration configuration = new Configuration();
        DefinitionClassifier documentClassifier = new DefinitionClassifier();
        //convertRawToProcessedCorpus(rawFolder, ProcessedFolder);
        try {
            String fileName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";
            String htmlText = Utils.readStringFromFile(fileName);
            Document doc = Parser.parseDocumentFromHtml(htmlText);

            // annotate
            Pipeline<Document, Document> pipeline =
                    new Pipeline.Builder()
                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                            .build();
            doc = pipeline.process(doc);


            List<String> defList = new ArrayList<String>();
            int count = 0;
            for(CoreMap paragraph : doc.getParagraphs()) {
                if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                    String words = Joiner.on(",").join(DocumentHelper
                            .getTokenString(
                                    paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class)));

                    defList.add(paragraph.getId() + "\t" + words + "\t" + paragraph.getText());

                }
            }
            System.out.println(defList);
            // definition only
            Pipeline<List<String>, List<String>> pDefTerms =
                    new Pipeline.Builder()
                            .add(Pipes.LIST_TO_CSV_FILE, Lists.newArrayList(configuration.get("model.persist.folder") + "defList"))
                            .build();

            pDefTerms.process(defList);

            documentClassifier.train(doc);
            documentClassifier.persistModel();

        } catch (Exception ex) {
            ex.printStackTrace();
            fail("failed testTrain");
        }


    }
}
