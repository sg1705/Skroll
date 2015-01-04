package com.skroll.parser.tokenizer;

import com.google.common.base.Joiner;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

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

        for(CoreMap paragraph : htmlDoc.getParagraphs()) {
            System.out.println(Joiner.on(',').join(paragraph.getTokens()));
        }
        System.out.println(htmlDoc.getParagraphs().size());
        assert (htmlDoc.getParagraphs().size() == 1659);
    }
}