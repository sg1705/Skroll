package com.skroll.parser.tokenizer;

import com.skroll.BaseTest;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.PhantomJsExtractor;
import com.skroll.parser.extractor.TestMode;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * Created by saurabh on 3/8/15.
 */
public class PageBreakAnnotationTest extends BaseTest {

    @Test
    public void testPageBreak() throws Exception {
        PhantomJsExtractor.TEST_MODE = TestMode.ON;
        // read a sample file
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = parser.parseDocumentFromHtml(htmlString);

        //iterate over each paragraph
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countPageBreak = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsPageBreakAnnotation.class)) {
                countPageBreak++;
            }
        }
        System.out.println(countPageBreak);
        assert ( countPageBreak == 121);
    }

    @Test
    public void testPageBreak10k() throws Exception {
        // read a sample file
        String fileName = "src/main/resources/parser/extractor/jQuery/form-10k-g.html";
        PhantomJsExtractor.TEST_MODE = TestMode.ON;
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = parser.parseDocumentFromHtml(htmlString);

        //iterate over each paragraph
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countPageBreak = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsPageBreakAnnotation.class)) {
                countPageBreak++;
            }
        }
        System.out.println(countPageBreak);

        assert (countPageBreak == 99);
    }

}
