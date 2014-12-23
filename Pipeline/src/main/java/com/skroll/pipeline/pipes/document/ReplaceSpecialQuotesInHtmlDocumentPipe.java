package com.skroll.pipeline.pipes.document;

import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.SyncPipe;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class ReplaceSpecialQuotesInHtmlDocumentPipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {
        List<Paragraph> newList = new ArrayList<Paragraph>();
        for(Paragraph paragraph : input.getParagraphs()) {
            String str = paragraph.getText();
            str = str.replace("\u201c","\"");
            str = str.replace("\u201d","\"");
            paragraph.setText(str);
            newList.add(paragraph);
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
