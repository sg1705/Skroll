package com.skroll.document;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import org.junit.Test;

public class DocumentHelperTest {


    @Test
    public void testGetMatchedText() throws Exception {
        Document doc = Parser.parseDocumentFromHtmlFile("src/test/resources/classifier/smaller-indenture.html");
        for(CoreMap coreMap: doc.getParagraphs()){
            DocumentHelper.setMatchedText(coreMap, DocumentHelper.getTokens(Lists.newArrayList("becontinuing", ",")), Paragraph.TOC_CLASSIFICATION);
            if(coreMap.containsKey(CoreAnnotations.IsTOCAnnotation.class)) {
                System.out.println(Joiner.on("").join(DocumentHelper.getTOCLists(coreMap)));
                assert(Joiner.on("").join(DocumentHelper.getTOCLists(coreMap)).equals("becontinuing,"));
            }

        }

    }
}