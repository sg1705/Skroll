package com.skroll.parser.tokenizer;

import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.util.List;

/**
 * Created by saurabh on 3/8/15.
 */
public class TextNodeHasAnchorTest extends TestCase {

    public void testTextNodeHasAnchor() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/document/text-node-has-anchor.html";
        String htmlString = Utils.readStringFromFile(fileName);


        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //find out how many tokens have bold
        List<Token> tokens = htmlDoc.get(CoreAnnotations.TokenAnnotation.class);
        int count = 0;
        for(Token token: tokens) {
            String text = token.get(CoreAnnotations.TextAnnotation.class);
            if (text.contains("4.10")) {
                count = 1;
                break;
            }
        }
        assert ( count == 1);
    }

}
