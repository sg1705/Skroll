package com.skroll.parser.tokenizer;

import com.skroll.document.Document;
import com.skroll.document.Entity;
import com.skroll.document.Document;
import com.skroll.document.Paragraph;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class RemoveBlankParagraphFromHtmlDocumentPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        //TODO some instance where there is just a blank like (maybe because of new line)
        List<Entity> newList = new ArrayList<Entity>();
        for(Entity paragraph : input.getParagraphs()) {
            String str = paragraph.getText().replace("\u00a0", "");
            if (!StringUtil.isBlank(str)) {
                //paragraph.setText(paragraph.getText().toLowerCase());
                paragraph.set(CoreAnnotations.TextAnnotation.class, paragraph.getText());
                newList.add(paragraph);
            }
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
