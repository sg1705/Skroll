package com.skroll.parser.extractor;

import com.skroll.document.Document;
import com.skroll.document.ModelHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;
import org.apache.commons.exec.*;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by saurabh on 1/16/15.
 */
public class PhantomJsExtractor extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        //extract html from document
        String htmlText = input.get(CoreAnnotations.TextAnnotation.class);
        //remove newline
        htmlText = htmlText.replace("\n","");

        //CollectingLogOutputStream logOutputStream = new CollectingLogOutputStream();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler( stdout );

        CommandLine cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN);
        cmdLine.addArgument(Constants.JQUERY_PARSER_JS);
        cmdLine.addArgument(htmlText);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        executor.setStreamHandler(psh);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
        executor.setWatchdog(watchdog);
        try {
            int exitValue = executor.execute(cmdLine);
        } catch (Exception e) {

        }
        String result = stdout.toString();
        ModelHelper helper = new ModelHelper();
        Document newDoc = new Document();
        try {
            newDoc = helper.fromJson(result);
        } catch (Exception e) {
            // error TODO needs to be logged
            e.printStackTrace();
        }
        newDoc.set(CoreAnnotations.TextAnnotation.class, htmlText);
        return newDoc;
    }

    //create a method to create a commandline

    public static class CollectingLogOutputStream extends LogOutputStream {

        private final List<String> lines = new LinkedList<String>();
        @Override protected void processLine(String line, int level) {
            lines.add(line);
        }

        public List<String> getLines() {
            return lines;
        }
    }


}

