package com.skroll.analyzer.evaluate;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.Models;
import com.skroll.document.Document;
import com.skroll.document.Entity;
import com.skroll.document.Token;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by saurabh on 12/28/14.
 */
public class Tester {

    public static Document testNaiveBayes(Document htmlDoc) {
        //create a pipeline
        Pipeline<Document, Document> pipeline =
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


    public static Document testHiddenMarketModel(Document htmlDoc, int category) {
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.HTML_DOCUMENT_HIDDEN_MARKOV_MODEL_TESTING_PIPE,
                                Lists.newArrayList((Object) Models.getHmmModel(), category))
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
        return htmlDoc;
    }

    public static double testDocclassifier(Document doc) {
        //create a pipeline
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();

        doc = pipeline.process(doc);

        List<Entity> paragraphs = doc.getParagraphs();
        Set<String> words = new HashSet<String>();
        for(Entity paragraph: paragraphs) {
            List<Token> tokens = paragraph.getTokens();
            for (Token token : tokens) {
                words.add(token.getText());
            }
        }

        Pipeline<List<String>, Double> evaluatePipeline =
                new Pipeline.Builder()
                        .add(Pipes.BINARY_SIMPLE_NAIVE_BAYES_TESTER,
                                Lists.newArrayList((Object) Models.getBinaryNaiveBayesModel()))
                        .build();
        double probability = evaluatePipeline.process(new ArrayList(words));

        return probability;
    }




}
