package com.skroll.parser.tokenizer;

import com.skroll.BaseTest;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.List;

/**
 * Created by saurabh on 3/8/15.
 */
public class CleanInternalAnchorLinksTest extends BaseTest {

    @Test
    public void testCleanInternalAnchorLinks() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/document/test-with-anchor-tags.html";
        String htmlString = Utils.readStringFromFile(fileName);

        int indexOfEdgar = htmlString.lastIndexOf("http://www.sec.gov/Archives/edgar/data/1288776/000119312513028362/d452134d10k.htm#toc");
        assert( indexOfEdgar > 0);

        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = parser.parseDocumentFromHtml(htmlString);

        indexOfEdgar = htmlDoc.getTarget().lastIndexOf("http://www.sec.gov/Archives/edgar/data/1288776/000119312513028362/d452134d10k.htm#toc");
        assert( indexOfEdgar == -1);

        indexOfEdgar = htmlDoc.getTarget().lastIndexOf("#toc");
        assert(indexOfEdgar > 0);

    }

}
