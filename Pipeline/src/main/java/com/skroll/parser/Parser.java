package com.skroll.parser;

import com.skroll.document.Document;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;

/**
 * Created by saurabh on 12/28/14.
 */
public class Parser {

    public static Document parseDocumentFromHtml(String html) {
        Document doc = new Document();
        doc.setSource(html);
        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        doc = pipeline.process(doc);
        return doc;
    }

    public static Document parseDocumentFromHtmlFile(String fileName) {

        String htmlText = "";
        try {
            htmlText = Utils.readStringFromFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document doc = parseDocumentFromHtml(htmlText);
        return doc;
    }


}
