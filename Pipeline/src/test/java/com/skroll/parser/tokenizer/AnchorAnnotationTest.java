package com.skroll.parser.tokenizer;

import com.skroll.BaseTest;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Utils;
import org.junit.Test;

import java.util.List;

/**
 * Created by saurabh on 3/8/15.
 */
public class AnchorAnnotationTest extends BaseTest {

    @Test
    public void testSimpleAnchorAnnotation() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/document/simple-html-text.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = parser.parseDocumentFromHtml(htmlString);

        //iterate over each paragraph
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countAnchorTags = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsAnchorAnnotation.class)) {
                System.out.println(paragraph.getText());
                countAnchorTags++;
            }
        }
        System.out.println(countAnchorTags);
        assert ( countAnchorTags == 1);
    }

    @Test
    public void testAnchorAnnotationOn10k() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/document/random10k.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = parser.parseDocumentFromHtml(htmlString);

        //iterate over each paragraph
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countAnchorTags = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsAnchorAnnotation.class)) {
                System.out.println(paragraph.getText());
                countAnchorTags++;
            }
        }
        System.out.println(countAnchorTags);
        assert ( countAnchorTags == 9);
    }
}
