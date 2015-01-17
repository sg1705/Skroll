package com.skroll.parser.tokenizer;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotation;
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
        List<Token> documentTokens = new ArrayList<Token>();
        for(CoreMap paragraph : input.getParagraphs()) {
            //get fragments
            List<CoreMap> fragments = paragraph.get(CoreAnnotations.ParagraphFragmentAnnotation.class);
            //create an empty list of tokens
            List<Token> tokens = new ArrayList<Token>();
            //iterate over each fragment
            for(CoreMap fragment : fragments ) {
                //get text of fragment
                String fragmentText = fragment.get(CoreAnnotations.TextAnnotation.class);
                //find if bold, italic or underline
                boolean isFragmentBold = fragment.containsKey(CoreAnnotations.IsBoldAnnotation.class);
                boolean isFragmentItalic = fragment.containsKey(CoreAnnotations.IsItalicAnnotation.class);
                boolean isFragmentUnderline = fragment.containsKey(CoreAnnotations.IsUnderlineAnnotation.class);
                //token the fragment
                List<String> words = pipeline.process(fragmentText);
                //iterate over each identified token
                for(String word: words) {
                    Token token = new Token();
                    token.setText(word);
                    if (isFragmentBold) {
                        token.set(CoreAnnotations.IsBoldAnnotation.class, true);
                    }
                    if (isFragmentUnderline) {
                        token.set(CoreAnnotations.IsUnderlineAnnotation.class, true);
                    }
                    if (isFragmentItalic) {
                        token.set(CoreAnnotations.IsItalicAnnotation.class, true);
                    }
                    tokens.add(token);
                }
            }
            //add tokens for the paragraph
            paragraph.set(CoreAnnotations.TokenAnnotation.class, tokens);
            //add tokens to master list
            documentTokens.addAll(tokens);
            if (tokens.size() > 0)
                newList.add(paragraph);

        }
        input.setParagraphs(newList);
        input.set(CoreAnnotations.TokenAnnotation.class, documentTokens);
        return this.target.process(input);
    }

}
