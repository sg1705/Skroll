package com.skroll.parser;

import com.skroll.analyzer.evaluate.Tester;
import com.skroll.analyzer.train.Trainer;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.parser.linker.TrainingAndTestingTest;
import junit.framework.TestCase;
import org.junit.Test;

public class ParserTest extends TestCase {

    @Test
    public void testParseDocumentForNB() throws Exception {
        TrainingAndTestingTest.trainNB();

        String testingFile = "src/test/resources/html-docs/random-indenture.html";
        Document htmlDoc = Tester.testNaiveBayes(
                Parser.parseDocumentFromHtmlFile(testingFile));

        int defCount = 0;
        for(CoreMap paragraph : htmlDoc.getParagraphs()) {
            if (DocumentHelper.isDefinition(paragraph)) {
                defCount++;
                System.out.println(paragraph.getText());
            }
        }
        System.out.println(defCount);
        assert(defCount == 149);

    }


}