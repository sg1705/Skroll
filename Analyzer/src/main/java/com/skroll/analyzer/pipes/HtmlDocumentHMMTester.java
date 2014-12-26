package com.skroll.analyzer.pipes;

import com.google.common.collect.Lists;
import com.skroll.analyzer.hmm.HiddenMarkovModel;
import com.skroll.analyzer.nb.BinaryNaiveBayesModel;
import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
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
public class HtmlDocumentHMMTester extends SyncPipe<HtmlDocument, HtmlDocument> {

    @Override
    public HtmlDocument process(HtmlDocument input) {
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);
        //chunk the document
        Pipeline<List<String>, List<Double>> testPipeline =
                new Pipeline.Builder()
                        .add(Pipes.HIDDEN_MARKOV_MODEL_SIMPLE_TESTING_PIPE,
                                Lists.newArrayList((Object) model,
                                new Integer(Constants.CATEGORY_POSITIVE)))
                        .build();

        //assume that words are extracted
        for(Paragraph paragraph : input.getParagraphs()) {
            List<String> definitions = new ArrayList<String>();
            if (paragraph.isDefinition()) {
                // test for terms
                List<Double> hmmResults = testPipeline.process(paragraph.getWords());
                int ii = 0;
                for (double prob : hmmResults) {
                    if (prob > Constants.DEF_THRESHOLD_PROBABILITY) {
                        // chances are that this is a definition
                        definitions.add(paragraph.getWords().get(ii));
                    }
                    ii++;
                }

            }
            paragraph.setDefinitions(definitions);
        }

        return this.target.process(input);
    }
}
