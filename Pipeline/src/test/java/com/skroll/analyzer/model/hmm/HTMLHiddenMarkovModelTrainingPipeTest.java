package com.skroll.analyzer.model.hmm;

import com.google.common.collect.Lists;
import com.skroll.document.Document;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;
import org.junit.Test;

import java.util.Arrays;

public class HTMLHiddenMarkovModelTrainingPipeTest extends TestCase {

    @Test
    public void testProcess() throws Exception {
        // read a sample file
        String fileName = "src/test/resources/analyzer/html-docs/random-indenture.html";
        String htmlString = Utils.readStringFromFile(fileName);

        Document htmlDoc= new Document();
        htmlDoc.setSource(htmlString);

        // create HMM document
        HiddenMarkovModel model = new HiddenMarkovModel(16);

        //create a pipeline
        Pipeline<Document, Document> pipeline =

                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.REMOVE_STARTING_SPACES_IN_HTML_DOC)
                                //.add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE,
                                Lists.newArrayList((Object) model))
                        .build();
        Document doc = pipeline.process(htmlDoc);
        model.updateProbabilities();;
        System.out.println(model.showProbabilities());
        System.out.println(model.showCounts());

        String[] token={"\"","Agreement","\"","has", "the", "meaning", "specified", "in", "the", "introductory", "paragraph", "hereof"};
        String[] token2={"\"","affiliate","\"","means","respect"};
        String[] token3={"affiliate","means","respect"};
        String[] token4={"\"","indenture","security", "holder","\"","means", "a", "holder", "of", "a", "note" };

//        System.out.println(Arrays.deepToString(document.infer(token)) );
        System.out.println(Arrays.deepToString(model.infer(token2)) );
//        System.out.println(Arrays.deepToString(document.infer(token4)) );


    }
}