package com.skroll.analyzer.hmm;

import com.google.common.collect.Lists;
import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.util.List;

public class HTMLHiddenMarkovModelTestingPipeTest extends TestCase {

    public void testProcess() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        String htmlString = Utils.readStringFromFile(fileName);

        HtmlDocument htmlDoc= new HtmlDocument();
        htmlDoc.setSourceHtml(htmlString);

        // create HMM model
        HiddenMarkovModel model = new HiddenMarkovModel(16);

        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE,
                                Lists.newArrayList((Object) model))
                        .build();
        HtmlDocument doc = pipeline.process(htmlDoc);
        model.updateProbabilities();;
        System.out.println(model.showProbabilities());
        System.out.println(model.showCounts());

        // read a sample file
        fileName = "src/test/resources/html-docs/random-indenture.html";
        htmlString = Utils.readStringFromFile(fileName);

        htmlDoc= new HtmlDocument();
        htmlDoc.setSourceHtml(htmlString);

        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> testingDocPipe =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .build();
        HtmlDocument testDoc = testingDocPipe.process(htmlDoc);

        Pipeline<HtmlDocument, List<double[][]>> testingPipe =
                new Pipeline.Builder()
                        .add(Pipes.HTML_HIDDEN_MARKOV_MODEL_TESTING_PIPE,
                                Lists.newArrayList((Object) model))
                        .build();
        List<double[][]> output = testingPipe.process(testDoc);
        List<Paragraph> paragraphs = testDoc.getParagraphs();
        for (int i=0; i<paragraphs.size();i++){
            System.out.println(paragraphs.get(i).getText());
            System.out.println(paragraphs.get(i).getWords());

            for (double[] probs: output.get(i)){
                System.out.printf("%.3f, ", probs[1]);
            }
            System.out.println();
            // System.out.println(Arrays.deepToString( output.get(i)));
        }

    }
}