package com.skroll.index;

import com.google.common.io.Files;
import com.skroll.document.Document;
import com.skroll.document.JsonDeserializer;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.extractor.TestMode;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.CmdLineExecutor;
import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;


/**
 * Creates Index for a given document object
 */
public class IndexCreator implements CmdLineExecutor {

    public static final Logger logger = LoggerFactory.getLogger(IndexCreator.class);

    // modes
    public static int TEST_MODE = TestMode.OFF;
    private static String NODE_COMMAND = "node";
    private String parser;

    public IndexCreator(String parser) {
        this.parser = parser;
    }

    private static String INPUT_FILE_ARGUMENT = "--inputFile";


    /**
     * Returns document after generating indexes
     * @param input input document
     * @return document parsed document
     * @throws Exception
     */
    public Document process(Document input) throws Exception {
        long startTime = System.currentTimeMillis();
        if (input.get(CoreAnnotations.SearchIndexAnnotation.class) == null) {
            //create file
            String json = JsonDeserializer.getJson(input);
            String inputFileName = "/tmp/" + startTime + ".json";
            Files.write(json.getBytes(Constants.DEFAULT_CHARSET), new File(inputFileName));
            input = process(input, inputFileName);
            new File(inputFileName).delete();
        }
        return input;
    }

    /**
     *
     * @param input input document
     * @return document parsed document
     * @throws Exception
     */
    public Document process(Document input, String inputFileName) throws Exception {
        long startTime = System.currentTimeMillis();
        CommandLine cmdLine = getIndexCreatorJsCommandLine(inputFileName);
        String parserOutput = execute(cmdLine, 0);
        logger.info("[{}]ms total index creation time", (System.currentTimeMillis() - startTime));
        input.set(CoreAnnotations.SearchIndexAnnotation.class, parserOutput);
        return input;
    }


    private CommandLine getIndexCreatorJsCommandLine(String inputFileName) {
        //default command line is linux
        CommandLine cmdLine = CommandLine.parse(NODE_COMMAND);
        //setup command line arguments
        cmdLine.addArgument(parser);
        cmdLine.addArgument(INPUT_FILE_ARGUMENT);
        cmdLine.addArgument(inputFileName);
        return cmdLine;
    }
}



