package com.skroll.parser.tokenizer;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei2learn on 1/11/2015.
 */
public class RemoveStartingSpacesInHtmlDocumentPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        List<CoreMap> newList = new ArrayList<CoreMap>();
        for(CoreMap paragraph : input.getParagraphs()) {
            String str = paragraph.getText();
            str = str.replaceFirst(" +","");
            paragraph.set(CoreAnnotations.TextAnnotation.class, str);

            newList.add(paragraph);
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}