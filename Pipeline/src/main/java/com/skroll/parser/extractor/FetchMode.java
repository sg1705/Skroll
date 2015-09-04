package com.skroll.parser.extractor;

import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Utils;
import org.apache.commons.exec.CommandLine;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by saurabh on 8/29/15.
 */
public class FetchMode implements ExtractorMode {

    public static int URL = 0;
    public static int FILE = 1;

    public static void preProcessTestMode(Document document, CommandLine cmdLine, int mode) throws Exception {
        String fileName = "none";
        String fetchUrl = "false";
        if (mode == FILE) {
            fetchUrl = "false";
            //create tmp file
            fileName = createTempFile(document.getSource()).toString();
        } else {
            fetchUrl = "true";
        }
        cmdLine.addArgument(fetchUrl);
        cmdLine.addArgument(fileName);
        if (document.containsKey(CoreAnnotations.SourceUrlAnnotation.class)) {
            String basePath = getBasePath(document.get(CoreAnnotations.SourceUrlAnnotation.class));
            cmdLine.addArgument(basePath);
            cmdLine.addArgument(document.get(CoreAnnotations.SourceUrlAnnotation.class));
        }
    }


    public static Document postProcessParseMode(String[] parserOutput, Document input, Document output, int mode) throws Exception {
        if (mode == URL) {
            //replace target
            output.setSource(parserOutput[4]);
            output.set(CoreAnnotations.SourceUrlAnnotation.class, input.get(CoreAnnotations.SourceUrlAnnotation.class));
        } else {
            output.setSource(input.getSource());
        }
        return output;

    }


    /**
     * Creates a temp file for a given string
     * @param htmlText html to be saved in the file
     * @return path of the created file
     * @throws Exception
     */
    private static Path createTempFile(String htmlText) throws Exception {
        Path path = Files.createTempFile("phantom", ".html");
        Utils.writeToFile(path.toString(), htmlText);
        return path;
    }

    private static String getBasePath(String url) throws Exception {
        int lastIndexOfSlash = url.lastIndexOf('/');
        return url.substring(0, lastIndexOfSlash);
    }


}
