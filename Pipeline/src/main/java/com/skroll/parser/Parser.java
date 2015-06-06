package com.skroll.parser;

import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.extractor.ParserException;
import com.skroll.parser.tokenizer.PostExtractionPipe;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;

/**
 * Created by saurabh on 12/28/14.
 */
public class Parser {

    public static final int VERSION = 1;

    private static Document parseInDoc(Document document) throws ParserException {
        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.POST_EXTRACTION_PIPE)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        document = pipeline.process(document);
        setVersion(document);
        return document;
    }


    /**
     * Returns the parsed document from Html file.
     *
     * @param htmlText
     * @return document
     */
    public static Document parseDocumentFromHtml(String htmlText) throws ParserException {
        Document document = new Document();
        document.setSource(htmlText);
        return parseInDoc(document);
    }

    /**
     * Returns the parsed document from Html file.
     *
     * @param htmlText
     * @return document
     */
    public static Document parseDocumentFromHtml(String htmlText, String url) throws ParserException {
        Document document = new Document();
        document.setSource(htmlText);
        document.set(CoreAnnotations.SourceUrlAnnotation.class, url);
        return parseInDoc(document);
    }



    /**
     * Returns a parsed document from a file
     *
     * @param fileName
     * @return document
     */
    public static Document parseDocumentFromHtmlFile(String fileName) throws ParserException {

        String htmlText = "";
        try {
            htmlText = Utils.readStringFromFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document document = parseDocumentFromHtml(htmlText);
        return document;
    }

    private static void setVersion(Document doc) {
        doc.set(CoreAnnotations.ParserVersionAnnotationInteger.class, VERSION);
    }

}