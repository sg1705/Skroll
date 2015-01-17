package com.skroll.parser.annotator;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TypesafeMap;
import com.skroll.document.Annotator;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipe;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

public class FirstEightWordsFormatAnnotatorTest extends TestCase {

    @Test
    public void testCheckRequirements() throws Exception {
        String fileName = "src/test/resources/parser/extractor/experiment-jsoup-node-extraction.html";
        //create annotator
        Annotator annotator = new FirstEightWordsFormatAnnotator();
        //get a document
        Document doc = Parser.parseDocumentFromHtmlFile(fileName);
        List<Class<? extends TypesafeMap.Key>> req = annotator.requirements();
        assert (req.size() == 2);
    }

    public void testIsParagraphBold() throws Exception {
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        //create annotator
        Pipe<Document, Document> annotator = new FirstEightWordsFormatAnnotator();
        //get a document
        Document doc = Parser.parseDocumentFromHtmlFile(fileName);
        annotator.process(doc);

        List<CoreMap> paragraphs = doc.getParagraphs();
        int boldCount = 0;
        int italicCount = 0;
        int underlineCount = 0;
        for(CoreMap paragraph : paragraphs) {
            boolean isbold = paragraph.get(CoreAnnotations.IsBoldAnnotation.class);
            if (isbold) {
                System.out.println(paragraph.getText());
                boldCount++;
            }

            boolean isItalic = paragraph.get(CoreAnnotations.IsItalicAnnotation.class);
            if (isItalic) {
                System.out.println(paragraph.getText());
                italicCount++;
            }

            boolean isUnderline = paragraph.get(CoreAnnotations.IsUnderlineAnnotation.class);
            if (isUnderline) {
                System.out.println(paragraph.getText());
                underlineCount++;
            }

        }
        System.out.println(boldCount);
        System.out.println(italicCount);
        System.out.println(underlineCount);
    }
}