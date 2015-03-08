package com.skroll.parser.tokenizer;

import com.google.common.base.Strings;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Tokenzies parsed document
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
            //create an empty string buffer
            StringBuffer buf = new StringBuffer();
            //boolean to see if each fragment is center
            boolean isCenterAligned = true;
            //figure out font size
            String paraFontSize = "";
            String prevFontSize = "";
            //paragraph level bold, underline, italic
            boolean isParaBold = true;
            boolean isParaItalic = true;
            boolean isParaUnderline = true;
            //count tokens that starts with upper case
            int startsWithUppercaseCount = 0;
            //iterate over each fragment
            for(CoreMap fragment : fragments ) {
                //get text of fragment
                String fragmentText = fragment.get(CoreAnnotations.TextAnnotation.class);
                //append to buffer to saving in paragraph annotation the entire text
                buf.append(fragmentText);
                //find if bold, italic or underline
                boolean isFragmentBold = fragment.containsKey(CoreAnnotations.IsBoldAnnotation.class);
                isParaBold = isParaBold && isFragmentBold;
                boolean isFragmentItalic = fragment.containsKey(CoreAnnotations.IsItalicAnnotation.class);
                isParaItalic = isParaItalic && isFragmentItalic;
                boolean isFragmentUnderline = fragment.containsKey(CoreAnnotations.IsUnderlineAnnotation.class);
                isParaUnderline = isParaUnderline && isFragmentUnderline;
                isCenterAligned = isCenterAligned &&  fragment.containsKey(CoreAnnotations.IsCenterAlignedAnnotation.class);
                //process fontsize
                if (paraFontSize.equals("")) {
                    paraFontSize = fragment.get(CoreAnnotations.FontSizeAnnotation.class);
                    prevFontSize = fragment.get(CoreAnnotations.FontSizeAnnotation.class);
                } else {
                    if (!prevFontSize.equals(fragment.get(CoreAnnotations.FontSizeAnnotation.class))) {
                        paraFontSize = paraFontSize + fragment.get(CoreAnnotations.FontSizeAnnotation.class);

                    }
                    prevFontSize = fragment.get(CoreAnnotations.FontSizeAnnotation.class);
                }

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
                    //count token for starts with upper case
                    //is token a special character
                    if (!Constants.SPECIAL_STOP_WORDS.contains(token.getText())) {
                        //now that we know it is not a special character
                        if (Character.isUpperCase(token.getText().charAt(0))) {
                            startsWithUppercaseCount++;
                        }

                    }
                }
            }
            //add concatenated string to paragraph
            paragraph.set(CoreAnnotations.TextAnnotation.class, buf.toString());
            //add tokens for the paragraph
            paragraph.set(CoreAnnotations.TokenAnnotation.class, tokens);
            //check to see if it is all upper case
            if (buf.toString().equals(buf.toString().toUpperCase())) {
                //this is all upper case
                paragraph.set(CoreAnnotations.IsUpperCaseAnnotation.class, true);
            }
            //set annotation for center aligned
            if (isCenterAligned) {
                paragraph.set(CoreAnnotations.IsCenterAlignedAnnotation.class, true);
            }
            // set paragraph level font finger print
            paragraph.set(CoreAnnotations.FontSizeAnnotation.class, paraFontSize);
            //set annotation for bold, italic, underline
            if (isParaBold) {
                paragraph.set(CoreAnnotations.IsBoldAnnotation.class, true);
            }
            if (isParaItalic) {
                paragraph.set(CoreAnnotations.IsItalicAnnotation.class, true);
            }
            if (isParaUnderline) {
                paragraph.set(CoreAnnotations.IsUnderlineAnnotation.class, true);
            }
            //set the count of tokens that start with uppercase
            paragraph.set(CoreAnnotations.TokenStartsWithUpperCaseCount.class, startsWithUppercaseCount);
            //add tokens to master list
            documentTokens.addAll(tokens);
            // check to see if it is a page break
            if ((tokens.size() > 0) || exceptions(paragraph))
                newList.add(paragraph);

        }
        input.setParagraphs(newList);
        input.set(CoreAnnotations.TokenAnnotation.class, documentTokens);
        return this.target.process(input);
    }

    private boolean exceptions(CoreMap paragraph) {
        if (paragraph.containsKey(CoreAnnotations.IsPageBreakAnnotation.class)) {
            return true;
        }
        return false;
    }

}
