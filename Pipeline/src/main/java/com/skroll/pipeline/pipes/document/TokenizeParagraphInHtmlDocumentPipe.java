package com.skroll.pipeline.pipes.document;

import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class TokenizeParagraphInHtmlDocumentPipe extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {

        // create a pipeline for each word
        Pipeline<String, List<String>> pipeline = new Pipeline.Builder<List<String>, List<String>>()
                .add(Pipes.STOP_WORD_FILTER)
                .build();


        List<Paragraph> newList = new ArrayList<Paragraph>();
        for(Paragraph paragraph : input.getParagraphs()) {
            String paragraphText = paragraph.getText();
            List<String> words = pipeline.process(paragraphText);
            paragraph.setWords(words);
            if (words.size() > 0)
               newList.add(paragraph);
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
