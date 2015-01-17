package com.skroll.parser.extractor;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.ModelHelper;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import org.apache.commons.exec.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * cd by saurabh on 1/16/15.
 */
public class PhantomJsExtractor {

    public Document process(Document input) throws Exception {
        //extract html from document
        String htmlText = input.get(CoreAnnotations.TextAnnotation.class);
        //remove newline
        htmlText = htmlText.replace("\n","");

        //create tmp file
        String fileName = createTempFile(htmlText).toString();

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler( stdout );

        CommandLine cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN);
        cmdLine.addArgument(Constants.JQUERY_PARSER_JS);
        cmdLine.addArgument(fileName);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        executor.setStreamHandler(psh);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
        executor.setWatchdog(watchdog);
        int exitValue = 0;
        try {
            exitValue = executor.execute(cmdLine);
        } catch (Exception e) {
            //TODO throw exception
            throw e;
        }

        if (exitValue != 1) {
            throw new Exception("Cannot parse the file. Phantom exited with the return code:" + exitValue);
        }

        String result = stdout.toString();
        ModelHelper helper = new ModelHelper();
        Document newDoc = new Document();
        try {
            newDoc = helper.fromJson(result);
        } catch (Exception e) {
            // error TODO needs to be logged
            e.printStackTrace();
            throw e;
        }
        newDoc.set(CoreAnnotations.TextAnnotation.class, htmlText);

        //no paragraphs in the doc
        if (newDoc.get(CoreAnnotations.ParagraphsAnnotation.class) == null) {
            throw new Exception("No paragraphs were identified:");
        }
        newDoc = postExtraction(newDoc);
        return newDoc;
    }


    private Path createTempFile(String htmlText) throws Exception {
        Path path = Files.createTempFile("phantom", ".html");
        Utils.writeToFile(path.toString(), htmlText);
        return path;
    }

    /**
     * Process a given document for the following.
     *
     * Tokenize PragraphFragments
     * Create TextAnnotation for Paragraph
     *
     * @param doc
     * @return
     */
    private Document postExtraction(Document doc) {
        //create TextAnnotation for paragraph
        List<CoreMap> paragraphs = doc.getParagraphs();
        for(CoreMap paragraph: paragraphs) {
            StringBuffer buf = new StringBuffer();
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
