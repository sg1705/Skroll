package com.skroll.analyzer.model.hmm;

import com.google.common.base.Splitter;
import com.skroll.document.*;
import com.skroll.pipeline.SyncPipe;

import java.util.*;

/**
 * Trains a document with the given HtmlDocument
 *
 * Created by saurabh on 12/21/14.
 */
public class HTMLHiddenMarkovModelTrainingPipe extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);

        List<Entity> paragraphs = input.getParagraphs();

        for( Entity paragraph : paragraphs) {
            List<String> tokens = DocumentHelper.getTokenString(paragraph.getTokens());

            HashSet<String> definitionsSet;
            if (paragraph.getChildEntity(EntityType.DefinedTermsAnnotation).getTokens().size() == 0){
                definitionsSet= new HashSet<String>();
            } else {
                List<Token> defTokens = paragraph.getChildEntity(EntityType.DefinedTermsAnnotation).getTokens();
                List<String> definitions = Splitter.on(' ').splitToList(DocumentHelper.getTokenString(defTokens).get(0));
                definitionsSet = new HashSet<String>(definitions);
            }

            int[] tokenType = new int[tokens.size()];
            int ii = 0;
            for(String token : tokens) {
                if (definitionsSet.contains(token)) {
                    tokenType[ii] = 1;
                } else {
                    tokenType[ii] = 0;
                }
                ii++;
            }


            model.updateCounts(tokens
                    .toArray(new String[tokens.size()]), tokenType);
        }
        return this.target.process(input);
    }


}
