package com.skroll.pipeline.pipes.document;

import com.google.common.base.Joiner;
import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

public class ParagraphsRemoveBlankPipeTest extends TestCase {

    public void testProcess() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        String htmlString = Utils.readStringFromFile(fileName);

        HtmlDocument htmlDoc= new HtmlDocument();
        htmlDoc.setSourceHtml(htmlString);

        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        HtmlDocument doc = pipeline.process(htmlDoc);

        for(Paragraph paragraph : htmlDoc.getParagraphs()) {
            System.out.println(Joiner.on(',').join(paragraph.getWords()));
        }
        System.out.println(htmlDoc.getParagraphs().size());
        assert (htmlDoc.getParagraphs().size() == 1655);
    }
}