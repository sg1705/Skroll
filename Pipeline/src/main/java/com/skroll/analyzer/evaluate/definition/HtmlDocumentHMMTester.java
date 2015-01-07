package com.skroll.analyzer.evaluate.definition;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;

import java.util.ArrayList;
import java.util.List;

/**
 * This class assumes that there is a HtmlDocument available
 * Created by saurabh on 12/23/14.
 */
public class HtmlDocumentHMMTester extends SyncPipe<Document, Document> {

    @Override
    public Document process(Document input) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);
        //chunk the document
        Pipeline<List<String>, List<Double>> testPipeline =
                new Pipeline.Builder()
                        .add(Pipes.HIDDEN_MARKOV_MODEL_SIMPLE_TESTING_PIPE,
                                Lists.newArrayList((Object) model,
                                new Integer(Constants.CATEGORY_POSITIVE)))
                        .build();

        List<CoreMap> newParagraphs = new ArrayList<CoreMap>();
        //assume that words are extracted
        for(CoreMap paragraph : input.getParagraphs()) {
            List<String> definitions = new ArrayList<String>();
            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                boolean isPreviousWordDefinition = false;
                List<String> tempDefinitions = new ArrayList<String>();
                // test for terms
                List<String> tokens = DocumentHelper.getTokenString(
                        paragraph.get(CoreAnnotations.TokenAnnotation.class));
                List<Double> hmmResults = testPipeline.process(tokens);
                int ii = 0;
                for (double prob : hmmResults) {
                    if (prob > Constants.DEF_THRESHOLD_PROBABILITY) {
                        if (!isPreviousWordDefinition) {
                            isPreviousWordDefinition = true;
                            tempDefinitions.add(paragraph.get(CoreAnnotations.TokenAnnotation.class).get(ii).getText());
                        } else {
                            tempDefinitions.add(paragraph.get(CoreAnnotations.TokenAnnotation.class).get(ii).getText());
                        }
//                        // chances are that this is a definition
//                        definitions.add(paragraph.getWords().get(ii));
                    } else {
                        isPreviousWordDefinition = false;
                    }
                    if (!isPreviousWordDefinition && (tempDefinitions.size() > 0)) {
                        // add it to paragraph
                        definitions.add(Joiner.on(" ").join(tempDefinitions));
                        tempDefinitions = new ArrayList<String>();
                    }
                    ii++;
                }
                if (tempDefinitions.size() > 0) {
                    // add it to paragraph
                    definitions.add(Joiner.on(" ").join(tempDefinitions));
                }
            }
            paragraph.set(CoreAnnotations.DefinedTermsAnnotation.class, DocumentHelper.createTokens(definitions));
            newParagraphs.add(paragraph);
        }
        input.setParagraphs(newParagraphs);
        return this.target.process(input);
    }
}
