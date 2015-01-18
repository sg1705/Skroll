package com.skroll.parser.tokenizer;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
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
        List<CoreMap> newList = new ArrayList<CoreMap>();
        for(CoreMap paragraph : input.getParagraphs()) {
            String str = paragraph.getText().replace("\u00a0", "");
            str = str.replaceFirst("\u0020+","");
            if (!StringUtil.isBlank(str)) {
                paragraph.set(CoreAnnotations.TextAnnotation.class, str);
                newList.add(paragraph);
            }
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
