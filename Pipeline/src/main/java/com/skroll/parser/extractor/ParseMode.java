package com.skroll.parser.extractor;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.ModelHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Utils;
import org.apache.commons.exec.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Created by saurabh on 8/29/15.
 */
public class ParseMode implements ExtractorMode {

    public static int PARTIAL = 0;
    public static int FULL = 1;

    public static void preProcessTestMode(Document document, CommandLine cmdLine, int mode) throws Exception {
    }


    public static Document postProcessParseMode(String[] parserOutput, Document input, Document output, int mode) throws Exception {
        ModelHelper helper = new ModelHelper();
        Document newDoc = new Document();
        if (mode == FULL) {
            newDoc = helper.fromJson(parserOutput[1]);
            //no paragraphs in the doc
            if (newDoc.get(CoreAnnotations.ParagraphsAnnotation.class) == null) {
                throw new Exception("No paragraphs were identified:");
            }
            newDoc = postExtraction(newDoc);
        }
        return newDoc;

    }

    /**
     * Process a given document for the following.
     *
     * Tokenize ParagraphFragments
     * Create TextAnnotation for Paragraph
     *
     * @param doc document to process
     * @return document object
     */
    private static Document postExtraction(Document doc) {
        //create TextAnnotation for paragraph
        List<CoreMap> paragraphs = doc.getParagraphs();
        for(CoreMap paragraph: paragraphs) {
            StringBuilder buf = new StringBuilder();
            List<CoreMap> fragments = paragraph.get(CoreAnnotations.ParagraphFragmentAnnotation.class);
            for(CoreMap fragment : fragments) {
                String text = fragment.get(CoreAnnotations.TextAnnotation.class);
                buf.append(text);
            }
            paragraph.set(CoreAnnotations.TextAnnotation.class, buf.toString());
        }
        return doc;
    }

}
