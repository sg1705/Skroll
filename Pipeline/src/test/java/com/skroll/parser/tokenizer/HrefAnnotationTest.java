package com.skroll.parser.tokenizer;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Utils;
import org.junit.Test;

import java.util.List;

/**
 * Created by saurabh on 6/6/15.
 */
public class HrefAnnotationTest {

    @Test
    public void testHrefAnnotation() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/document/test-with-anchor-tags.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //iterate over each paragraph
        List<CoreMap> paragraphs = htmlDoc.getParagraphs();
        int countAnchorTags = 0;
        for (CoreMap paragraph : paragraphs) {
            if (paragraph.containsKey(CoreAnnotations.IsHrefAnnotation.class)) {
                System.out.println(paragraph.getText());
                countAnchorTags++;
            }
        }
        System.out.println(countAnchorTags);
        assert ( countAnchorTags == 3);

    }
}
