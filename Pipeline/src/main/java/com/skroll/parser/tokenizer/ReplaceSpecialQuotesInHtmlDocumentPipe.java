package com.skroll.parser.tokenizer;

import com.skroll.document.Document;
import com.skroll.document.Entity;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class ReplaceSpecialQuotesInHtmlDocumentPipe extends SyncPipe<Document,Document> {

    @Override
    public Document process(Document input) {
        List<Entity> newList = new ArrayList<Entity>();
        for(Entity paragraph : input.getParagraphs()) {
            String str = paragraph.getText();
            str = str.replace("\u201c","\"");
            str = str.replace("\u201d","\"");
            paragraph.set(CoreAnnotations.TextAnnotation.class, str);
            newList.add(paragraph);
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
