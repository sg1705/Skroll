package com.skroll.parser.extractor;

import com.skroll.document.Document;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.CmdLineExecutor;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;

/**
 * cd by saurabh on 1/16/15.
 */
public class PhantomJsExtractor implements CmdLineExecutor {

    public static final Logger logger = LoggerFactory.getLogger(PhantomJsExtractor.class);

    private static String PARA_DELIMITTER = "(<!--sk)|(sk-->)";
    private static String OUTPUT_DELIMITTER = ";-skroll.io-;";
    // modes
    public static int TEST_MODE = TestMode.OFF;
    private static int FETCH_MODE;
    private static int PARSE_MODE;

    /**
     *
     * @param input input document
     * @return document parsed document
     * @throws Exception
     */
    public Document process(Document input) throws Exception {
        long startTime = System.currentTimeMillis();

        CommandLine cmdLine = getPhantomJsCommandLine();
        TestMode.preProcessTestMode(input, cmdLine, TEST_MODE);
        FetchMode.preProcessTestMode(input, cmdLine, FETCH_MODE);
        String[] parserOutput = executePhantomJsExtractor(cmdLine);
        Document output = new Document();
        output = ParseMode.postProcessParseMode(parserOutput, input, output, PARSE_MODE);
        output = FetchMode.postProcessParseMode(parserOutput, input, output, FETCH_MODE);
        output.setTarget(parserOutput[2].replaceAll(PARA_DELIMITTER, ""));
        logger.info("[{}]ms taken by jQuery during parsing", parserOutput[3]);
        logger.info("[{}]ms total extraction time", (System.currentTimeMillis() - startTime));
        return output;
    }


    private CommandLine getPhantomJsCommandLine() {
        //default command line is linux
        CommandLine cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN);
        if (System.getProperty("os.name").contains("windows")) {
            cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN_WINDOWS);
        } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN_MAC);
        }
        //setup command line arguments
        cmdLine.addArgument(Constants.JQUERY_PARSER_JS);
        return cmdLine;
    }

    private String[] executePhantomJsExtractor(CommandLine cmdLine) throws Exception {
        String[] parserOutput = execute(cmdLine, 1).split(OUTPUT_DELIMITTER);
        return parserOutput;
    }


    public static void setFetchMode(int fetchMode) {
        FETCH_MODE = fetchMode;
    }

    public static void setParseMode(int parseMode) {
        PARSE_MODE = parseMode;
    }


}


