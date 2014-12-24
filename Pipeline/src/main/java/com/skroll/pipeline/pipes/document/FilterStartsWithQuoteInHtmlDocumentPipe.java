package com.skroll.pipeline.pipes.document;

import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class FilterStartsWithQuoteInHtmlDocumentPipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {
        List<Paragraph> newList = new ArrayList<Paragraph>();
        for(Paragraph paragraph : input.getParagraphs()) {
            String str = paragraph.getText();
            if (str.startsWith(Constants.QUOTE)) {
                newList.add(paragraph);
            }
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
