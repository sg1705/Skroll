package com.skroll.parser.extractor;

import com.skroll.document.Document;
import com.skroll.pipeline.util.Constants;
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
public class PhantomJsExtractor {

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
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler( stdout );

        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        executor.setStreamHandler(psh);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(240000);
        executor.setWatchdog(watchdog);
        long executionTime = System.currentTimeMillis();
        int exitValue = executor.execute(cmdLine);
        if (exitValue != 1) {
            ParserException ps =  new ParserException("Cannot parse the file. Phantom exited with the return code:" + exitValue);
            ps.setReturnValue(exitValue);
            throw ps;
        }
        logger.info("Execution time:" + (System.currentTimeMillis() - executionTime));
        long splitTime = System.currentTimeMillis();
        byte[] output = stdout.toByteArray();
        String[] parserOutput = new String(output, Constants.DEFAULT_CHARSET)
                .split(OUTPUT_DELIMITTER);
        logger.info("Splitting time:" + (System.currentTimeMillis() - splitTime));
        return parserOutput;
    }


    public static void setFetchMode(int fetchMode) {
        FETCH_MODE = fetchMode;
    }

    public static void setParseMode(int parseMode) {
        PARSE_MODE = parseMode;
    }


}


