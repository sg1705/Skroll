package com.skroll.parser.tokenizer;

import com.skroll.BaseTest;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * Created by saurabh on 3/8/15.
 */
public class BoldAnnotationTest extends BaseTest {

    @Test
    public void testSimpleBoldAnnotation() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/document/simple-html-text.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = parser.parseDocumentFromHtml(htmlString);

        //find out how many tokens have bold
        //List<Token> tokens = htmlDoc.get(CoreAnnotations.TokenAnnotation.class);
        List<Token> tokens = DocumentHelper.getDocumentTokens(htmlDoc);
        int count = 0;
        for(Token token: tokens) {
            if (token.containsKey(CoreAnnotations.IsBoldAnnotation.class)) {
                System.out.println(token.getText());
                count++;
            }
        }
        System.out.println("Total number of bold words:" + count);
        assert ( count == 2);
    }

}
