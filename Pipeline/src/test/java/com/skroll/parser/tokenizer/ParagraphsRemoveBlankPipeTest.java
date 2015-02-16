package com.skroll.parser.tokenizer;

import com.google.common.base.Joiner;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.util.Iterator;
import java.util.List;

public class ParagraphsRemoveBlankPipeTest extends TestCase {

    public void testProcess() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        String htmlString = Utils.readStringFromFile(fileName);

        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        Document doc = pipeline.process(htmlDoc);

        System.out.println(doc.getParagraphs().size());
        assert (doc.getParagraphs().size() == 1384);
    }

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