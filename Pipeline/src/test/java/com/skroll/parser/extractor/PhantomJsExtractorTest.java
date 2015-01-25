package com.skroll.parser.extractor;

import com.google.common.collect.TreeTraverser;
import com.google.common.html.HtmlEscapers;
import com.google.common.io.Files;
import com.skroll.document.CoreMap;
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
    public void testPhantomJsExtract() throws Exception {
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        Document doc = new Document("<div>this is a awesome</div>");
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
    }


    @Test
    public void testPhantomJsNoScript() throws Exception {
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        Document doc = new Document("<script>//this is a comment</script><div>this is a awesome</div>");
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
        assert(doc.getParagraphs().size() == 3);
    }


    @Test
    public void testPhantomJsExtractHtmlFile() throws Exception {
        String fileName = "src/test/resources/analyzer/hmmTrainingDocs/Tribune CA 2.html";
        String htmlText = Utils.readStringFromFile(fileName);
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        Document doc = new Document(htmlText);
        doc = phantomJsExtractor.process(doc);
        System.out.println(doc.getParagraphs().size());
        assert (doc.getParagraphs() != null);
        assert (doc.getParagraphs().size() == 7594);
    }

    @Test
    public void testPhantomJsSpace() throws Exception {
        String fileName = "src/main/resources/trainingDocuments/indentures/AMC Networks Indenture.html";
        String htmlText = Utils.readStringFromFile(fileName);
        PhantomJsExtractor phantomJsExtractor = new PhantomJsExtractor();
        Document doc = new Document(htmlText);
        doc = phantomJsExtractor.process(doc);
        for(CoreMap paragraph : doc.getParagraphs()) {
            System.out.println(paragraph.getText());
        }
        System.out.println(doc.getParagraphs().size());
        //assert (doc.getParagraphs() != null);
        //assert (doc.getParagraphs().size() == 7594);
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