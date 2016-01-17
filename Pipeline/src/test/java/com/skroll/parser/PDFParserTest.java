package com.skroll.parser;

import com.skroll.document.Document;
import com.skroll.document.DocumentFormat;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Test;

import javax.xml.bind.DatatypeConverter;

import java.util.Arrays;

import static org.junit.Assert.*;

public class PDFParserTest {

    @Test
    public void testParsePartialPDFFromUrl() throws Exception {
        String pdfUrl = "https://investor.google.com/pdf/20141231_google_10K.pdf";
        byte[] originalPdf = PDFParser.fetchContent(pdfUrl);
        String fileName = "aa";
        Document pdfDoc = PDFParser.parsePartialPDFFromUrl(pdfUrl);
        pdfDoc.setId(fileName);
        assert(pdfDoc.get(CoreAnnotations.DocumentFormatAnnotationInteger.class) == DocumentFormat.PDF.id());
        //convert both to byte array and compare
        byte[] byteArrayFromDoc = DatatypeConverter.parseBase64Binary(pdfDoc.getSource());
        assert(Arrays.equals(byteArrayFromDoc, originalPdf));
        assert(byteArrayFromDoc.length == originalPdf.length);
        assert(byteArrayFromDoc.length == 4079152);
        assert(pdfDoc.get(CoreAnnotations.ParserVersionAnnotationInteger.class) == PDFParser.VERSION);
        assert(pdfDoc.get(CoreAnnotations.SourceUrlAnnotation.class).equals(pdfUrl));
        assert(pdfDoc.get(CoreAnnotations.IsPartiallyParsedAnnotation.class));
    }
}