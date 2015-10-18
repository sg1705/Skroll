package com.skroll.util;

import com.skroll.parser.extractor.ParserException;
import com.skroll.pipeline.util.Constants;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;

/**
 * Allows a given class to be have a command line execution
 * <p>
 * Created by saurabh on 9/27/15.
 */
public interface CmdLineExecutor {

    public static final Logger logger = LoggerFactory.getLogger(CmdLineExecutor.class);

    /**
     * Execute command line
     *
     * @param cmdLine
     * @param expectedReturnCode
     * @return
     * @throws Exception
     */
    default public String execute(CommandLine cmdLine, int expectedReturnCode) throws Exception {
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler(stdout);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(expectedReturnCode);
        executor.setStreamHandler(psh);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(240000);
        executor.setWatchdog(watchdog);
        long executionTime = System.currentTimeMillis();
        int exitValue = executor.execute(cmdLine);
        if (exitValue != expectedReturnCode) {
            ParserException ps = new ParserException("Cannot obtain index. Node exited with the return code:" + exitValue);
            ps.setReturnValue(exitValue);
            throw ps;
        }
        logger.info("Execution time:" + (System.currentTimeMillis() - executionTime));
        long splitTime = System.currentTimeMillis();
        byte[] output = stdout.toByteArray();
        String parserOutput = new String(output, Constants.DEFAULT_CHARSET);
        logger.info("Fetching standard output time:" + (System.currentTimeMillis() - splitTime));
        return parserOutput;
    }
}
