package com.skroll.parser.tokenizer;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Utils;
import org.junit.Test;

import java.util.List;

/**
 * Created by saurabh on 5/9/15.
 */
public class TablesAnnotationTest {

    @Test
    public void testSimpleTablesAnnotation() throws Exception {
        String htmlString = "<table><tr><td>Only 1 column</td></tr></table";
        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //find out how many tokens have bold
        List<CoreMap> tables = htmlDoc.get(CoreAnnotations.TablesAnnotation.class);
        List<CoreMap> rows = tables.get(0).get(CoreAnnotations.RowsAnnotation.class);
        List<CoreMap> cols = rows.get(0).get(CoreAnnotations.ColsAnnotation.class);
        List<CoreMap> fragments = cols.get(0).get(CoreAnnotations.ParagraphFragmentAnnotation.class);
        String paraId = htmlDoc.getParagraphs().get(0).getId();
        String paraIdInTable = tables.get(0).get(CoreAnnotations.ParagraphIdAnnotation.class);
        System.out.println(paraId);
        assert ( tables.size() == 1);
        assert ( rows.size() == 1);
        assert ( cols.size() == 1);
        assert ( fragments.size() == 1);
        assert(paraId.equals(paraIdInTable));
    }

    @Test
    public void testTablesAnnotationWithMultipleFragments() throws Exception {
        String htmlString = "<table><tr><td><b>Only 1</b> column</td></tr></table";
        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //find out how many tokens have bold
        List<CoreMap> tables = htmlDoc.get(CoreAnnotations.TablesAnnotation.class);
        List<CoreMap> rows = tables.get(0).get(CoreAnnotations.RowsAnnotation.class);
        List<CoreMap> cols = rows.get(0).get(CoreAnnotations.ColsAnnotation.class);
        List<CoreMap> fragments = cols.get(0).get(CoreAnnotations.ParagraphFragmentAnnotation.class);
        String paraId = htmlDoc.getParagraphs().get(0).getId();
        String paraIdInTable = tables.get(0).get(CoreAnnotations.ParagraphIdAnnotation.class);
        System.out.println(paraId);
        assert ( tables.size() == 1);
        assert ( rows.size() == 1);
        assert ( cols.size() == 1);
        assert ( fragments.size() == 2);
        assert(paraId.equals(paraIdInTable));
    }

    @Test
    public void testTablesAnnotationWithFile() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/document/test-table.html";
        String htmlString = Utils.readStringFromFile(fileName);
        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);
        htmlDoc = Parser.parseDocumentFromHtml(htmlString);

        //find out how many tokens have bold
        List<CoreMap> tables = htmlDoc.get(CoreAnnotations.TablesAnnotation.class);
        List<CoreMap> rows = tables.get(0).get(CoreAnnotations.RowsAnnotation.class);
        List<CoreMap> cols = rows.get(0).get(CoreAnnotations.ColsAnnotation.class);
        List<CoreMap> fragments = cols.get(0).get(CoreAnnotations.ParagraphFragmentAnnotation.class);
        String paraId = htmlDoc.getParagraphs().get(2).getId();
        String paraIdInTable = tables.get(0).get(CoreAnnotations.ParagraphIdAnnotation.class);
        System.out.println(paraId);
        assert ( tables.size() == 2);
        assert ( rows.size() == 6);
        assert ( cols.size() == 7);
        assert ( fragments.size() == 0);
        assert(paraId.equals(paraIdInTable));
    }


}
