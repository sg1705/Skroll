package com.skroll.parser;

import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.common.io.ByteStreams;
import com.skroll.document.Document;
import com.skroll.document.DocumentFormat;
import com.skroll.document.annotation.CoreAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;

/**
 * Created by saurabh on 1/2/16.
 */
public class PDFParser {

    public static final int VERSION = 1;
    static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
    public static final Logger logger = LoggerFactory.getLogger(PDFParser.class);

    /**
     * Returns a parsed PDF document from a given URL
     *
     * @param url
     * @return parsed document
     */
    public static final Document parsePartialPDFFromUrl(String url) {
        Document document = null;
        try {
            //create a new document object
            document = new Document();
            document.setSource(DatatypeConverter.printBase64Binary(fetchContent(url)));
            document.set(CoreAnnotations.SourceUrlAnnotation.class, url);
            document.set(CoreAnnotations.IsPartiallyParsedAnnotation.class, true);
            setVersion(document);
            setDocumentFormat(document);
            //create a content proto

        } catch (IOException e) {
            logger.warn("PDF at [%1] could not be fetched", url, e);
        }

        return document;
    }

    /**
     * Method to fetch byte array from a given url.
     * This is used to fetch PDF from a remote URL.
     *
     * @param url
     * @return byte[] containing contents of the given url
     * @throws IOException
     */
    public static final byte[] fetchContent(String url) throws IOException {
        HttpRequestFactory requestFactory =
                HTTP_TRANSPORT.createRequestFactory();
        HttpEncoding encoding = new GZipEncoding();
        GenericUrl genericUrl = new GenericUrl(url);
        byte[] content = new byte[0];
        try {
            HttpRequest request = requestFactory.buildGetRequest(genericUrl).setEncoding(encoding);
            content = ByteStreams.toByteArray(request.execute().getContent());

        } catch (IOException e) {
            logger.warn("PDF at [%1] could not be fetched", url, e);
        }
        return content;
    }

    private static void setVersion(Document doc) {
        doc.set(CoreAnnotations.ParserVersionAnnotationInteger.class, VERSION);
    }

    private static void setDocumentFormat(Document doc) {
        doc.set(CoreAnnotations.DocumentFormatAnnotationInteger.class, DocumentFormat.PDF.id());
    }

}
