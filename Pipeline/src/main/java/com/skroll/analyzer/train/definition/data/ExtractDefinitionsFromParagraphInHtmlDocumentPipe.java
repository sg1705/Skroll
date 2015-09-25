package com.skroll.analyzer.train.definition.data;

import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;
import com.skroll.util.WordHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * todo: need to replace this with good training data.
 * Created by sagupta on 12/14/14.
 */
public class ExtractDefinitionsFromParagraphInHtmlDocumentPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
//        CoreMap para = new CoreMap();
//        List<Token> emptyTokenList = new ArrayList<>();
//        para.set(CoreAnnotations.TokenAnnotation.class, emptyTokenList);
//        input.getParagraphs().add(0, para);

        for(CoreMap paragraph : input.getParagraphs()) {
            List<Token> tokens = paragraph.getTokens();
            if (tokens.size()==0) continue; // skip empty paragraphs
            if (WordHelper.isQuote(tokens.get(0).getText())){
                List<Token> definedTerms = new ArrayList<>();
                for (int i = 1; i < tokens.size() && !WordHelper.isQuote(tokens.get(i).getText()); i++) {
                     definedTerms.add(tokens.get(i));
                }
//                CategoryAnnotationHelper.setDefinedTermTokensInParagraph(definedTerms, paragraph);
                CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategory(paragraph, Arrays.asList(definedTerms), Category.DEFINITION);
                System.out.println(paragraph.getText());
            }

        }
        return this.target.process(input);
//        return input;
    }

}
