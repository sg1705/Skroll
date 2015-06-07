package com.skroll.parser.extractor;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.ModelHelper;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.ByteArrayOutputStream;

import java.nio.file.Files;
import java.nio.file.Path;
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
        //htmlText = "<meta charset=\"utf-8\" />" + htmlText;

        //create tmp file
        String fileName = createTempFile(htmlText).toString();

        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler( stdout );

        //default command line is linux
        CommandLine cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN);
        if (System.getProperty("os.name").contains("windows")) {
            cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN_WINDOWS);
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN_MAC);
        }
        cmdLine.addArgument(Constants.JQUERY_PARSER_JS);
        cmdLine.addArgument(fileName);
        if (input.containsKey(CoreAnnotations.SourceUrlAnnotation.class)) {
            System.out.println(input.get(CoreAnnotations.SourceUrlAnnotation.class));
            cmdLine.addArgument(input.get(CoreAnnotations.SourceUrlAnnotation.class));
        }
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        executor.setStreamHandler(psh);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(120000);
        executor.setWatchdog(watchdog);
        int exitValue = 0;
        try {
            exitValue = executor.execute(cmdLine);
        } catch (Exception e) {
            //TODO throw exception
            throw e;
        }

        if (exitValue != 1) {
            ParserException ps =  new ParserException("Cannot parse the file. Phantom exited with the return code:" + exitValue);
            ps.setReturnValue(exitValue);
            throw ps;
        }


        byte[] output = stdout.toByteArray();
        String[] parserOutput = new String(output, Constants.DEFAULT_CHARSET).split(";---------------SKROLLJSON---------------------;");
        String[] result = parserOutput[1].split(";---------------SKROLL---------------------;");

        ModelHelper helper = new ModelHelper();
        Document newDoc = new Document();
        try {
            newDoc = helper.fromJson(result[0]);
            newDoc.setTarget(result[1]);
            newDoc.setSource(htmlText);
        } catch (Exception e) {
            // error TODO needs to be logged
            e.printStackTrace();
            throw e;
        }
        //newDoc.set(CoreAnnotations.TextAnnotation.class, htmlText);

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

