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
public class RemoveBlankParagraphFromHtmlDocumentPipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {
        //TODO some instance where there is just a blank like (maybe because of new line)
        List<Paragraph> newList = new ArrayList<Paragraph>();
        for(Paragraph paragraph : input.getParagraphs()) {
            String str = paragraph.getText().replace("\u00a0", "");
            if (!StringUtil.isBlank(str)) {
                //paragraph.setText(paragraph.getText().toLowerCase());
                paragraph.setText(paragraph.getText());
                newList.add(paragraph);
            }
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
