package com.skroll.analyzer.hmm;

import com.google.common.collect.Lists;
import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.util.Arrays;
import java.util.List;

public class HTMLHiddenMarkovModelTestingPipeTest extends TestCase {

    public void testProcess() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/html-docs/random-indenture.html";
        String htmlString = Utils.readStringFromFile(fileName);

        HtmlDocument htmlDoc= new HtmlDocument();
        htmlDoc.setSourceHtml(htmlString);

        // create HMM model
        HiddenMarkovModel model = new HiddenMarkovModel(20);

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
        List<Paragraph> paragraphs = testDoc.getParagraphs();

        List<double[][]> probabilities = testingPipe.process(testDoc);
        for (int i=0; i<paragraphs.size();i++){
            System.out.println(paragraphs.get(i).getText());
//                System.out.println(paragraphs.get(i).getWords().size()+", "+probabilities.get(i).length);
            int k=0;
//            for (int j=0; j<Math.min(model.size(), paragraphs.get(i).getWords().size()); j++){
            for (int j=0; j< paragraphs.get(i).getWords().size() && k < model.size(); j++){

//                    System.out.println(i+", "+j);
                if (paragraphs.get(i).getWords().get(j).equals("\"")) continue; //skip quote
                System.out.printf("%d %s=%.2f ", j,paragraphs.get(i).getWords().get(j), probabilities.get(i)[k++][1]);
            }
            System.out.println();
        }
        //String[] token2={"\"","affiliate","\"","means","respect"};

//        String[] token={"\"","tt","&","c","\"","means","telemetry","tracking","control"};
        //String[] token={"\"","indenture","trustee","\"","or","\"","institutional","trustee","\"","means"};
//        String[] token={"\"","indenture","trustee","\"","or","\"","institutional","trustee","\"","means", "the"};
//
//
//        //String[] token={"\"","tt","c","\""};
//
//        System.out.println(Arrays.deepToString(model.infer(token)) );
//
//
//
//
//        double [] prob = {0.5,0.5};
//        System.out.println(model.showProbabilities());
//
//        String[] newTokens = new String[token.length];
//        int[][] features = new int[token.length][2];;
//        int length = model.createFeatures(token, newTokens, features);
//
//        System.out.println("state prob "+Arrays.toString(prob)+'\n');
//        System.out.println(Arrays.toString(model.inferJointProbabilitiesStateAndObservation(0, prob, newTokens, features)) );
    }
}