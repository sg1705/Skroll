package com.skroll.parser.tokenizer;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.PhantomJsExtractor;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by saurabh on 3/8/15.
 */
public class IsInTableAnnotationTest extends TestCase {

    public void testIsInTableAnnotation() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/document/test-table.html";
        String htmlString = Utils.readStringFromFile(fileName);
        PhantomJsExtractor.TEST_FLAGS = true;

        Document htmlDoc = new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //find out how many paragraphs have table annotation
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countTableAnnotation = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsInTableAnnotation.class)) {
                countTableAnnotation++;
            }
        }
        System.out.println(countTableAnnotation);
        assert (countTableAnnotation == 2);
    }

    public void testComplex10kIsInTableAnnotation() throws Exception {
        PhantomJsExtractor.TEST_FLAGS = true;
        // read a sample file
        String fileName = "src/test/resources/document/test-10k-table.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc = new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //find out how many paragraphs have table annotation
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countTableAnnotation = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsInTableAnnotation.class)) {
                countTableAnnotation++;
            }
        }
        System.out.println(countTableAnnotation);
        assert (countTableAnnotation == 1);
    }



    public void test10kIsInTableAnnotation() throws Exception {
        PhantomJsExtractor.TEST_FLAGS = true;
        // read a sample file
        String fileName = "src/test/resources/document/random10k.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc = new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //find out how many paragraphs have table annotation
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countTableAnnotation = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsInTableAnnotation.class)) {
                countTableAnnotation++;
            }
        }
        System.out.println(countTableAnnotation);
        assert (countTableAnnotation == 214);
    }
}
