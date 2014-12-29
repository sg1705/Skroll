package com.skroll.parser.tokenizer;

import com.google.common.base.Joiner;
import com.skroll.document.HtmlDocument;
import com.skroll.document.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by saurabh on 12/23/14.
 */
public class DocumentAllPipesTest extends TestCase {

    @Test
    public void testAllProcess() throws Exception {
        String fileName = "src/test/resources/parser/tokenizer/experiment-jsoup-node-extraction.html";
        String htmlText = Utils.readStringFromFile(fileName);

        HtmlDocument htmlDoc = new HtmlDocument(htmlText);

        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        HtmlDocument doc = pipeline.process(htmlDoc);

        for(Paragraph paragraph : htmlDoc.getParagraphs()) {
            String words = Joiner.on(",").join(paragraph.getWords());
            System.out.println(words);
        }
        System.out.println(htmlDoc.getParagraphs().size());
        assert (htmlDoc.getParagraphs().size() == 1953);
    }
}
