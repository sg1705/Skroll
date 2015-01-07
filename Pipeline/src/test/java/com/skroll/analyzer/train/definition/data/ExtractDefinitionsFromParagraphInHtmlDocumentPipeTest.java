package com.skroll.analyzer.train.definition.data;

import com.google.common.base.Joiner;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
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

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .build();
        Document doc = pipeline.process(htmlDoc);
        int count = 0;
        for(CoreMap paragraph : htmlDoc.getParagraphs()) {
                count++;
                DocumentHelper.getTokenString(
                        paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class));
                String words = Joiner.on(",").join(DocumentHelper
                        .getTokenString(
                                paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class)));
                System.out.println(words);
        }
        System.out.println(count);
        assert (count == 301);

    }
}