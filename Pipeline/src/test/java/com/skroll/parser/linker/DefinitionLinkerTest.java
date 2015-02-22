package com.skroll.parser.linker;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefinitionLinkerTest extends TestCase {

    @Test
    public void testLinkRandomDocument() throws Exception {
        //search for regex
        String validateString = "<a href=\"#2240\">";
        //load up a doc
        Document document = Parser.parseDocumentFromHtmlFile("src/test/resources/parser/linker/test-linker-random.html");
        // the paragraph for Agent's group is 2240
        //setup fake definition and tokens
        int linkCount = 0;
        for(CoreMap paragraph : document.getParagraphs()) {
            if (paragraph.getId().equals("2240")) {
                //artificially make it a definition
                paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, true);
                //iterate over each token and set these as defined terms
                //set index of tokens[1-4] as defined terms
                List<Token> definedTokens = new ArrayList();
                definedTokens.add(paragraph.getTokens().get(1));
                definedTokens.add(paragraph.getTokens().get(2));
                definedTokens.add(paragraph.getTokens().get(3));
                definedTokens.add(paragraph.getTokens().get(4));
                List<List<Token>> definedTokenList = new ArrayList();
                definedTokenList.add(definedTokens);
                paragraph.set(CoreAnnotations.DefinedTermListAnnotation.class,definedTokenList);
                // perform all the linking
                DefinitionLinker linker = new DefinitionLinker();
                document = linker.linkDefinition(document);
                //find how many places did it link
                Pattern pattern = Pattern.compile(validateString);
                Matcher matcher = pattern.matcher(document.getTarget());
                linkCount = 0;
                while (matcher.find()) {
                    linkCount++;
                }

                System.out.println("ParagraphId:" + paragraph.getId());
                System.out.println("ParagraphId:" + paragraph.getText() + "\n\n");
                System.out.println("Linked Agent's Group" + linkCount);
                break;
            }
        }
        assert (linkCount == 14);
        Utils.writeToFile("/tmp/linker-random-test.html", document.getTarget());
    }
}