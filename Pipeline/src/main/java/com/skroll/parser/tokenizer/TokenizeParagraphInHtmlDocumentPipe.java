package com.skroll.parser.tokenizer;

import com.skroll.document.HtmlDocument;
import com.skroll.document.Paragraph;
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
            paragraph.setTokens(words);
            if (words.size() > 0)
               newList.add(paragraph);
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
