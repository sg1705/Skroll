package com.skroll.analyzer.train.definition.data;

import com.google.common.base.Joiner;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

public class ExtractDefinitionsFromParagraphInHtmlDocumentPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        String fileName = "src/test/resources/analyzer/experiment-jsoup-node-extraction.html";
        String htmlText = Utils.readStringFromFile(fileName);

        Document htmlDoc = new Document(htmlText);
        //parse html into a document object
        htmlDoc = Parser.parseDocumentFromHtml(htmlText);
        //pipeline to filter out paragraphs that start with quote
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
        // extract definitions
        int count = 0;
        for(CoreMap paragraph : htmlDoc.getParagraphs()) {
                count++;
                ;
//                DocumentHelper.getTokenString(
//                        paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class));
                String words = Joiner.on(",").join(CategoryAnnotationHelper.getTokenStringsForACategory(paragraph, Category.DEFINITION));
                System.out.println(words);
        }
        System.out.println(count);
        assert (count == 307);
    }
}