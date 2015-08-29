package com.skroll.parser.extractor;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.ModelHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * cd by saurabh on 1/16/15.
 */
public class PhantomJsExtractor {

    public static final Logger logger = LoggerFactory.getLogger(PhantomJsExtractor.class);

    public static Boolean TEST_FLAGS = false;


    private boolean isFetchHtml = false;
    private String fileName = "none";

    private boolean isParsePartial = false;

    /**
     *
     * @param input input document
     * @return document parsed document
     * @throws Exception
     */
    public Document process(Document input) throws Exception {
        long startTime = System.currentTimeMillis();

        CommandLine cmdLine = getPhantomJsCommandLine();
        preProcessTestingCondition(input, cmdLine);
        preProcessFetchHtml(input, cmdLine);
        String[] parserOutput = executePhantomJsExtractor(cmdLine);
        Document output = new Document();
        output = postProcessParseMode(parserOutput, input, output);
        output = postProcessFetchMode(parserOutput, input, output);
        output.setTarget(parserOutput[2].replaceAll("(<!--sk)|(sk-->)", ""));
        logger.info("[{}]ms taken by jQuery during parsing", parserOutput[3]);
        logger.info("[{}]ms total extraction time", (System.currentTimeMillis() - startTime));
        return output;
    }


    public void setParsePartial(boolean isParsePartial) {
        this.isParsePartial = isParsePartial;
    }

    public void setFetchHtml(boolean isFetchHtml) {
        this.isFetchHtml = isFetchHtml;
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
                .split(";-skroll.io-;");
        logger.info("Splitting time:" + (System.currentTimeMillis() - splitTime));
        return parserOutput;
    }

    private void preProcessFetchHtml(Document document, CommandLine cmdLine) throws Exception {
        if (!isFetchHtml) {
            //create tmp file
            fileName = createTempFile(document.getSource()).toString();
        }
        cmdLine.addArgument(Boolean.toString(isFetchHtml));
        cmdLine.addArgument(fileName);
        if (document.containsKey(CoreAnnotations.SourceUrlAnnotation.class)) {
            String basePath = this.getBasePath(document.get(CoreAnnotations.SourceUrlAnnotation.class));
            cmdLine.addArgument(basePath);
            cmdLine.addArgument(document.get(CoreAnnotations.SourceUrlAnnotation.class));
        }
    }


    private void preProcessTestingCondition(Document document, CommandLine cmdLine) {
        cmdLine.addArgument(TEST_FLAGS.toString());
    }


    private Document postProcessParseMode(String[] parserOutput, Document input, Document output) throws Exception {
        ModelHelper helper = new ModelHelper();
        Document newDoc = new Document();
        if (!isParsePartial) {
            newDoc = helper.fromJson(parserOutput[1]);
            //no paragraphs in the doc
            if (newDoc.get(CoreAnnotations.ParagraphsAnnotation.class) == null) {
                throw new Exception("No paragraphs were identified:");
            }
            newDoc = postExtraction(newDoc);
        }
        return newDoc;
    }

    private Document postProcessFetchMode(String[] parserOutput, Document input, Document output) throws Exception {
        if (isFetchHtml) {
            //replace target
            output.setSource(parserOutput[4]);
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
    private Path createTempFile(String htmlText) throws Exception {
        Path path = Files.createTempFile("phantom", ".html");
        Utils.writeToFile(path.toString(), htmlText);
        return path;
    }

    /**
     * Process a given document for the following.
     *
     * Tokenize ParagraphFragments
     * Create TextAnnotation for Paragraph
     *
     * @param doc document to process
     * @return document object
     */
    private Document postExtraction(Document doc) {
        //create TextAnnotation for paragraph
        List<CoreMap> paragraphs = doc.getParagraphs();
        for(CoreMap paragraph: paragraphs) {
            StringBuilder buf = new StringBuilder();
            List<CoreMap> fragments = paragraph.get(CoreAnnotations.ParagraphFragmentAnnotation.class);
            for(CoreMap fragment : fragments) {
                String text = fragment.get(CoreAnnotations.TextAnnotation.class);
                buf.append(text);
            }
            paragraph.set(CoreAnnotations.TextAnnotation.class, buf.toString());
        }
        return doc;
    }

    private String getBasePath(String url) throws Exception {
        int lastIndexOfSlash = url.lastIndexOf('/');
        return url.substring(0, lastIndexOfSlash);
    }


}

