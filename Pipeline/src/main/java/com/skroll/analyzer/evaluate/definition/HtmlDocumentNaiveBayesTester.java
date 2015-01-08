package com.skroll.analyzer.evaluate.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;
import com.skroll.pipeline.util.Constants;

import java.util.List;

/**
 * This class assumes that there is a HtmlDocument available
 * Created by saurabh on 12/23/14.
 */
public class HtmlDocumentNaiveBayesTester extends SyncPipe<Document, Document> {

    private static final double DEF_THRESHOLD_PROBABILITY = 0.85;

    @Override
    public Document process(Document input) {
        NaiveBayes model = (NaiveBayes)config.get(0);
        //chunk the document
        Pipeline<List<String>, Integer> testPipeline =
                new Pipeline.Builder()
                        .add(Pipes.NAIVES_BAYES_SIMPLE_TESTING,
                                Lists.newArrayList((Object) model))
                        .build();

        //assume that words are extracted
        for(CoreMap paragraph : input.getParagraphs()) {
            //double isDefinition = testPipeline.process(DocumentHelper.getTokenString(paragraph.getTokens()));
            int category = testPipeline.process(DocumentHelper.getTokenString(paragraph.getTokens()));
            if (category == Constants.CATEGORY_POSITIVE) {
                paragraph.set(CoreAnnotations.IsDefinitionAnnotation.class, new Boolean(true));
            }
        }

        return this.target.process(input);
    }
}
