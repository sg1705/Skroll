package com.skroll.analyzer.evaluate;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.Models;
import com.skroll.model.HtmlDocument;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;

/**
 * Created by saurabh on 12/28/14.
 */
public class Tester {

    public static HtmlDocument testNaiveBayes(HtmlDocument htmlDoc) {
        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.HTML_DOC_BINARY_NAIVE_BAYES_TESTER,
                                Lists.newArrayList((Object) Models.getBinaryNaiveBayesModel()))
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
        return htmlDoc;
    }


    public static HtmlDocument testHiddenMarketModel(HtmlDocument htmlDoc, int category) {
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.HTML_DOCUMENT_HIDDEN_MARKOV_MODEL_TESTING_PIPE,
                                Lists.newArrayList((Object) Models.getHmmModel(), category))
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
        return htmlDoc;
    }




}
