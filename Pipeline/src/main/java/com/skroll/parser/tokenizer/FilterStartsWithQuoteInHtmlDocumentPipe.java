package com.skroll.parser.tokenizer;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class FilterStartsWithQuoteInHtmlDocumentPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        List<CoreMap> newList = new ArrayList<CoreMap>();
        for(CoreMap paragraph : input.getParagraphs()) {
            String str = paragraph.getText();
            if (str.startsWith(Constants.QUOTE)) {
                newList.add(paragraph);
            }
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
