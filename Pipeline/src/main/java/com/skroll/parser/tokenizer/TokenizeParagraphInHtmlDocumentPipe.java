package com.skroll.parser.tokenizer;

import com.skroll.document.*;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class TokenizeParagraphInHtmlDocumentPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {

        // create a pipeline for each word
        Pipeline<String, List<String>> pipeline = new Pipeline.Builder<List<String>, List<String>>()
                .add(Pipes.STOP_WORD_FILTER)
                .build();


        List<CoreMap> newList = new ArrayList<CoreMap>();
        for(CoreMap paragraph : input.getParagraphs()) {
            String paragraphText = paragraph.getText();
            List<String> words = pipeline.process(paragraphText);
            List<Token> tokens = DocumentHelper.getTokens(words);
            paragraph.set(CoreAnnotations.TokenAnnotation.class, tokens);
            if (tokens.size() > 0)
               newList.add(paragraph);
        }
        input.setParagraphs(newList);
        return this.target.process(input);
    }

}
