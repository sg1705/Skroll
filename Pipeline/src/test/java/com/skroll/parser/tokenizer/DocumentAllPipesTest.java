package com.skroll.parser.tokenizer;

import com.google.common.base.Joiner;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * Created by saurabh on 12/23/14.
 */
public class DocumentAllPipesTest extends TestCase {

    @Test
    public void testAllProcess() throws Exception {
        String fileName = "src/test/resources/parser/tokenizer/experiment-jsoup-node-extraction.html";

        Document doc = Parser.parseDocumentFromHtmlFile(fileName);
        //find out how many tokens have bold
        List<Token> tokens = doc.get(CoreAnnotations.TokenAnnotation.class);
        int count = 0;
        for(Token token: tokens) {
            if (token.containsKey(CoreAnnotations.IsBoldAnnotation.class)) {
                System.out.println(token.getText());
                count++;
            }
        }
        System.out.println("Total number of bold words:" + count);
        System.out.println("Total number of paragraphs:" + doc.getParagraphs().size());
        assert (count == 729);
        System.out.println(doc.getParagraphs().size());
        assert (doc.getParagraphs().size() == 1993);
    }
}
