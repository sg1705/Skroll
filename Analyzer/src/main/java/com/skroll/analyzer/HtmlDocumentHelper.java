package com.skroll.analyzer;

import com.skroll.model.HtmlDocument;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;

/**
 * Created by saurabh on 12/28/14.
 */
public class HtmlDocumentHelper {

    public static HtmlDocument getHtmlDocumentFromHtml(String html) {
        HtmlDocument htmlDoc = new HtmlDocument();
        htmlDoc.setSourceHtml(html);
        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
        return htmlDoc;
    }

    public static HtmlDocument getHtmlDocumentFromHtmlFile(String fileName) {

        String htmlText = "";
        try {
            htmlText = Utils.readStringFromFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        HtmlDocument htmlDoc = getHtmlDocumentFromHtml(htmlText);
        return htmlDoc;
    }


}
