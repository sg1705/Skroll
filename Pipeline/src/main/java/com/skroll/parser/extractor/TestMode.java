package com.skroll.parser.extractor;

import com.skroll.document.Document;
import org.apache.commons.exec.CommandLine;

/**
 * Created by saurabh on 8/29/15.
 */
public class TestMode implements ExtractorMode {

    public static int ON = 0;
    public static int OFF = 1;

    public static void preProcessTestMode(Document document, CommandLine cmdLine, int mode) throws Exception {
        if (mode == ON) {
            cmdLine.addArgument("true");
        } else {
            cmdLine.addArgument("false");
        }
    }


    public static Document postProcessParseMode(String[] parserOutput, Document input, Document output, int mode) throws Exception {
        return output;
    }
}
