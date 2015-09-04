package com.skroll.parser.extractor;

import com.skroll.document.Document;
import org.apache.commons.exec.CommandLine;

/**
 * Created by saurabh on 8/29/15.
 */
public interface ExtractorMode {

    static void preProcessTestMode(Document document, CommandLine cmdLine, int mode) throws Exception {

    };
    static Document postProcessParseMode(String[] parserOutput, Document input, Document output, int mode) throws Exception {
        return output;
    }


}
