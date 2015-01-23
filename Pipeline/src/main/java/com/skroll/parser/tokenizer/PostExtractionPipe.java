package com.skroll.parser.tokenizer;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;
import org.jsoup.helper.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabh on 1/18/15.
 */
public class PostExtractionPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        //TODO some instance where there is just a blank like (maybe because of new line)
        List<CoreMap> newParagraph = new ArrayList<CoreMap>();
        for(CoreMap paragraph : input.getParagraphs()) {
            List<CoreMap> newFragments = new ArrayList<>();
            List<CoreMap> fragments = paragraph.get(CoreAnnotations.ParagraphFragmentAnnotation.class);
            for(CoreMap fragment : fragments) {
                String fragmentText = fragment.get(CoreAnnotations.TextAnnotation.class);

                //eliminate fragment
                if (StringUtil.isBlank(fragmentText)) {
                    continue;
                }

                //replace nbsp with space
                fragmentText = fragmentText.replace("\u00a0", " ");


                //remove blanks from fragment text
                fragmentText = fragmentText.replace("\u00a0", "");
                //fragmentText = fragmentText.replaceFirst("\u0020+","");
                fragmentText = CharMatcher.BREAKING_WHITESPACE.trimLeadingFrom(fragmentText);

                //replace special quotes with single quote
                fragmentText = fragmentText.replace("\u201c","\"");
                fragmentText = fragmentText.replace("\u201d","\"");

                fragment.set(CoreAnnotations.TextAnnotation.class, fragmentText);
                newFragments.add(fragment);
            }
            if (newFragments.size() > 0) {
                paragraph.set(CoreAnnotations.ParagraphFragmentAnnotation.class, newFragments);
                newParagraph.add(paragraph);
            }
        }
        input.setParagraphs(newParagraph);

        return this.target.process(input);
    }

}
