package com.skroll.parser;

import com.skroll.document.Document;
import com.skroll.parser.tokenizer.PostExtractionPipe;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;

/**
 * Created by saurabh on 12/28/14.
 */
public class Parser {

    /**
     * Returns the parsed document from Html file.
     *
     * @param htmlText
     * @return document
     */
    public static Document parseDocumentFromHtml(String htmlText) {
        Document document = new Document();
        document.setSource(htmlText);
        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.POST_EXTRACTION_PIPE)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        document = pipeline.process(document);
        return document;
    }

    /**
     * Returns a parsed document from a file
     *
     * @param fileName
     * @return document
     */
    public static Document parseDocumentFromHtmlFile(String fileName) {

        String htmlText = "";
        try {
            htmlText = Utils.readStringFromFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document document = parseDocumentFromHtml(htmlText);
        return document;
    }

}