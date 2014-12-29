package com.skroll.analyzer.train.definition.data;

import com.skroll.document.HtmlDocument;
import com.skroll.document.Paragraph;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sagupta on 12/14/14.
 */
public class ExtractDefinitionsFromParagraphInHtmlDocumentPipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {

        for(Paragraph paragraph : input.getParagraphs()) {
            List<String> newList = new ArrayList<String>();
            String str = paragraph.getText();
            Pattern p = Pattern.compile( "\"([^\"]*)\"" );
            Matcher m = p.matcher( str );
            if (paragraph.getText().startsWith("\"")) {
                while (m.find()) {
                    newList.add(m.group(1));
                }
            }
            paragraph.setDefinitions(newList);
            if (newList.size() > 0) {
                paragraph.setDefinition(true);
            } else {
                paragraph.setDefinition(false);
            }
        }
        return this.target.process(input);
    }

}
