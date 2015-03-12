package com.skroll.parser.tokenizer;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by saurabh on 3/8/15.
 */
public class PageBreakAnnotationTest extends TestCase {
    public void testPageBreak() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //iterate over each paragraph
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countPageBreak = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsPageBreakAnnotation.class)) {
                System.out.println(paragraph.getText());
                countPageBreak++;
            }
        }
        System.out.println(countPageBreak);
        assert ( countPageBreak == 121);
    }

    public void testPageBreak10k() throws Exception {
        // read a sample file
        //String fileName = "src/main/resources/parser/extractor/jQuery/dish-10k.html";
        String fileName = "src/main/resources/parser/extractor/jQuery/brightcove-10k.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //iterate over each paragraph
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countPageBreak = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsPageBreakAnnotation.class)) {
                System.out.println(paragraph.getText());
                countPageBreak++;
            }
        }
        System.out.println(countPageBreak);
    }

}
