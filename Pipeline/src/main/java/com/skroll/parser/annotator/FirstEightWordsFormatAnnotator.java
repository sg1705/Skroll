package com.skroll.parser.annotator;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TypesafeMap;
import com.skroll.document.Annotator;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.pipes.SinkPipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sagupta on 12/14/14.
 */
public class FirstEightWordsFormatAnnotator extends SyncPipe<Document, Document> implements Annotator {

    public FirstEightWordsFormatAnnotator() {
        this.setTarget(new SinkPipe<Document, Document>());
    }

    @Override
    public Document process(Document input) {

        //process each paragraph in this document.
        List<CoreMap> paragraphs = input.getParagraphs();
        for(CoreMap paragraph : paragraphs) {
            boolean isBold = this.isFormat("b", paragraph);
            paragraph.set(CoreAnnotations.IsBoldAnnotation.class, isBold);

            boolean isItalic = this.isFormat("i", paragraph);
            paragraph.set(CoreAnnotations.IsItalicAnnotation.class, isItalic);

            boolean isUnderline = this.isFormat("u", paragraph);
            paragraph.set(CoreAnnotations.IsUnderlineAnnotation.class, isUnderline);

        }
        return this.target.process(input);
    }


    public boolean isFormat(String formatTag, CoreMap paragraph) {
        // get html text
        String htmlText = paragraph.get(CoreAnnotations.HTMLTextAnnotation.class);
        if (htmlText == null) {
            return false;
        }

        //parse the text
        org.jsoup.nodes.Document htmlDoc = Jsoup.parseBodyFragment(htmlText);
        Element boldElement = htmlDoc.select(formatTag).first();
        if (boldElement == null) {
            // no element with with format
            return false;
        }

        //check to see if first "n" words are of formatTag i.e. <b> or <i>

        // strategy .. bold elements are a textual subsequence of entire paragraph.
        // we can get the index of the sequence and then tokenize the string up to the bold subsequence
        // and get the number of tokens.

        String boldText = boldElement.text();
        String paragraphText = paragraph.getText();

        int indexOfBoldElement = paragraphText.indexOf(boldText);
        if (indexOfBoldElement < 0) {
            // something is wrong
            //ignore this element
            return false;
        }

        String textBeforeBoldStarts = paragraphText.substring(0, indexOfBoldElement);
        String[] tokenizeText = textBeforeBoldStarts.split(" ");

        if (tokenizeText.length < 12) {
            return true;
        }

        return false;
    }


    @Override
    public List<Class<? extends TypesafeMap.Key>> requirements() {
        List<Class<? extends TypesafeMap.Key>> list = new ArrayList<Class<? extends TypesafeMap.Key>>();
        list.add(CoreAnnotations.ParagraphsAnnotation.class);
        list.add(CoreAnnotations.HTMLTextAnnotation.class);

        return list;
    }

}
