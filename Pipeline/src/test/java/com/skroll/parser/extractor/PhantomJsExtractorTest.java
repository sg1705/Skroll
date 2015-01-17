package com.skroll.parser.extractor;

import com.google.common.collect.TreeTraverser;
import com.google.common.html.HtmlEscapers;
import com.google.common.io.Files;
import com.skroll.document.Document;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Iterator;

public class PhantomJsExtractorTest extends TestCase {

    @Test
    public void testPhantomExecution() {
        //CollectingLogOutputStream logOutputStream = new CollectingLogOutputStream();
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        PumpStreamHandler psh = new PumpStreamHandler( stdout );

        String htmlText = "<div>this is a awesome</div>";

        CommandLine cmdLine = CommandLine.parse(Constants.PHANTOM_JS_BIN);
        cmdLine.addArgument(Constants.JQUERY_PARSER_JS);
        cmdLine.addArgument(htmlText);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setExitValue(1);
        executor.setStreamHandler(psh);
        ExecuteWatchdog watchdog = new ExecuteWatchdog(60000);
        executor.setWatchdog(watchdog);
        int exitValue = 0;
        try {
            exitValue = executor.execute(cmdLine);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String result = stdout.toString();
        System.out.println(result);
        assert (exitValue == 1);
    }

    @Test
    public void testPhantomJsExtract() throws Exception {
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        Document doc = new Document("<div>this is a awesome</div>");
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());

    }

    @Test
    public void testPhantomJsExtractHtmlFile() throws Exception {
        String fileName = "src/test/resources/parser/extractor/experiment-jsoup-node-extraction.html";
        String htmlText = Utils.readStringFromFile(fileName);
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        Document doc = new Document(htmlText);
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
        assert (doc.getParagraphs() != null);
        assert (doc.getParagraphs().size() == 3286);
    }

    @Test
    public void testPhantomJSExtractorFromFolder() throws Exception {
        String folderName = "src/main/resources/trainingDocuments/indentures";
        Iterator<File> files = Files.fileTreeTraverser().children(new File(folderName)).iterator();
        while(files.hasNext()) {
            String fileName = files.next().toPath().toString();
            String htmlText = Utils.readStringFromFile(fileName);
            PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
            Document doc = new Document(htmlText);
            doc = phantomJsExtractor.process(doc);
            System.out.println(doc.getParagraphs().size());
            assert (doc.getParagraphs() != null);

        }

    }

}