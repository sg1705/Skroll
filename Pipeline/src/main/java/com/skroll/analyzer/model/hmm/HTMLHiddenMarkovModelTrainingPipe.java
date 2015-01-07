package com.skroll.analyzer.model.hmm;

import com.google.common.base.Splitter;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.SyncPipe;

import java.util.HashSet;
import java.util.List;

/**
 * Trains a document with the given HtmlDocument
 *
 * Created by saurabh on 12/21/14.
 */
public class HTMLHiddenMarkovModelTrainingPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);

        List<CoreMap> paragraphs = input.getParagraphs();

        for( CoreMap paragraph : paragraphs) {
            List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);

            HashSet<String> definitionsSet;
            if (!paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                definitionsSet= new HashSet<String>();
            } else {
                List<Token> defTokens = paragraph.get(CoreAnnotations.DefinedTermsAnnotation.class);
                List<String> definitions = Splitter.on(' ').splitToList(DocumentHelper.getTokenString(defTokens).get(0));
                definitionsSet = new HashSet<String>(definitions);
            }

            int[] tokenType = new int[tokens.size()];
            int ii = 0;
            for(Token token : tokens) {
                if (definitionsSet.contains(token.getText())) {
                    tokenType[ii] = 1;
                } else {
                    tokenType[ii] = 0;
                }
                ii++;
            }


            model.updateCounts(
                    DocumentHelper.getTokenString(tokens).toArray(new String[tokens.size()]),
                    tokenType);
        }
        return this.target.process(input);
    }


}
