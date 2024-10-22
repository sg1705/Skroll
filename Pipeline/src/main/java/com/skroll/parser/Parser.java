package com.skroll.parser;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentFormat;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.extractor.*;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import com.skroll.util.SkrollGuiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

/**
 * Created by saurabh on 12/28/14.
 */
public class Parser {

    public static final int VERSION = 4;
    public static final Logger logger = LoggerFactory.getLogger(Parser.class);
//    public static Injector injector = Guice.createInjector(new SkrollGuiceModule());

    @Inject
    PhantomJsExtractor phExtractor;


    @Inject
    public Parser(PhantomJsExtractor extractor) {
        this.phExtractor = extractor;
    }

    private Document parseInDoc(Document document, int fetchMode, int parseMode)
            throws ParserException {

        //create phantomjs extractor
//        PhantomJsExtractor phExtractor = injector.getInstance(PhantomJsExtractor.class);
        phExtractor.setFetchMode(fetchMode);
        phExtractor.setParseMode(parseMode);
        try {
            document = phExtractor.process(document);
            setVersion(document);
            setDocumentFormat(document);
        } catch (Exception e) {
            throw new ParserException(e);
        }

        if (parseMode == ParseMode.PARTIAL) {
            return document;
        }

        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.POST_EXTRACTION_PIPE)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        document = pipeline.process(document);
        return document;
    }

    /**
     * Returns the parsed document from Html file.
     *
     * @param url
     * @return document
     */
    public Document parsePartialDocumentFromUrl(String url) throws ParserException {
        Document document = new Document();
        document.set(CoreAnnotations.SourceUrlAnnotation.class, url);
        return parseInDoc(document, FetchMode.URL, ParseMode.PARTIAL);
    }

    /**
     * Returns the parsed document from Html file.
     *
     * @param htmlText
     * @return document
     */
    public Document parseDocumentFromHtml(String htmlText) throws ParserException {
        Document document = new Document();
        document.setSource(htmlText);
        return parseInDoc(document, FetchMode.FILE, ParseMode.FULL);
    }

    /**
     * Returns the parsed document from Html file.
     *
     * @param url
     * @return document
     */
    public Document parseDocumentFromUrl(String url) throws ParserException {
        Document document = new Document();
        document.set(CoreAnnotations.SourceUrlAnnotation.class, url);
        return parseInDoc(document, FetchMode.URL, ParseMode.FULL);
    }

    /**
     * Returns a parsed document from a file
     *
     * @param fileName
     * @return document
     */
    public Document parseDocumentFromHtmlFile(String fileName) throws ParserException {

        String htmlText = "";
        try {
            htmlText = Utils.readStringFromFile(fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Document document = parseDocumentFromHtml(htmlText);
        return document;
    }

    private void setVersion(Document doc) {
        doc.set(CoreAnnotations.ParserVersionAnnotationInteger.class, VERSION);
    }

    private void setDocumentFormat(Document doc) {
        doc.set(CoreAnnotations.DocumentFormatAnnotationInteger.class, DocumentFormat.HTML.id());
    }


    public Document reParse(Document document) throws ParserException {
        //get the source html
        if (document.getSource() == null) {
            //source is null
            logger.info("Source html is not available for [{}]", document.getId());
            String url = document.get(CoreAnnotations.SourceUrlAnnotation.class);
            if (url != null) {
                logger.info("Fetching source from [{}]", document.get(CoreAnnotations.SourceUrlAnnotation.class));
                try {
                    document.setSource(DocumentHelper.fetchHtml(url));
                } catch (Exception e) {
                    logger.error("Cannot fetch new html during re-parsing");
                    return document;
                }

            } else {
                logger.info("Source html is null and SourceUrl is null during reparsing");
                return document;
            }
            // will try to fetch from SourceUrl
        }
        Document newDoc = this.parseDocumentFromHtml(document.getSource());
        newDoc.setId(document.getId());
        // if parsed documents has different paragraphs then log error
        if (newDoc.getParagraphs().size() != document.getParagraphs().size()) {
            return document;
        }
        //set document level annotations in new doc
        if (document.containsKey(CoreAnnotations.SourceUrlAnnotation.class)) {
            newDoc.set(CoreAnnotations.SourceUrlAnnotation.class,
                    document.get(CoreAnnotations.SourceUrlAnnotation.class));
        }
        CategoryAnnotationHelper.copyAnnotations(document, newDoc);

        for(int ii = 0; ii < newDoc.getParagraphs().size(); ii++) {
            //copy annotations over
            CoreMap paragraph = document.getParagraphs().get(ii);
            CoreMap nPara = newDoc.getParagraphs().get(ii);
            CategoryAnnotationHelper.copyAnnotations(paragraph, nPara);
        }
        if (!document.equals(newDoc)) {
            logger.info("Reparsed document is not the same as the old doc. " +
                    "Number of paragraphs are different {}", document.get(CoreAnnotations.IdAnnotation.class));
            return document;
        }
        return newDoc;
    }

}
