package com.skroll.analyzer.evaluate.definition;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.SyncPipe;

import java.util.List;

/**
 * This class assumes that there is a HtmlDocument available
 * Created by saurabh on 12/23/14.
 */
public class HtmlDocumentNaiveBayesTester extends SyncPipe<HtmlDocument, HtmlDocument> {

    private static final double DEF_THRESHOLD_PROBABILITY = 0.85;

    @Override
    public HtmlDocument process(HtmlDocument input) {
        BinaryNaiveBayesModel model = (BinaryNaiveBayesModel)config.get(0);
        //chunk the document
        Pipeline<List<String>, Double> testPipeline =
                new Pipeline.Builder()
                        .add(Pipes.BINARY_SIMPLE_NAIVE_BAYES_TESTER,
                                Lists.newArrayList((Object) model))
                        .build();

        //assume that words are extracted
        for(Paragraph paragraph : input.getParagraphs()) {
            double isDefinition = testPipeline.process(paragraph.getWords());
            if (isDefinition > DEF_THRESHOLD_PROBABILITY)

                paragraph.setDefinition(true);
        }

        return this.target.process(input);
    }
}
