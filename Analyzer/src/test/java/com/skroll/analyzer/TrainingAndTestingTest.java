package com.skroll.analyzer;

import com.aliasi.classify.NaiveBayesClassifier;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.skroll.analyzer.hmm.HiddenMarkovModel;
import com.skroll.analyzer.nb.BinaryNaiveBayesModel;
import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.util.HashSet;
import java.util.List;

public class TrainingAndTestingTest extends TestCase {

    private HiddenMarkovModel hmmModel;
    private BinaryNaiveBayesModel nbModel;

    public void testProcess() throws Exception {

        String trainingFolder = "src/test/resources/hmmTrainingDocs";
        this.nbModel = trainNB();
        this.hmmModel = trainHMM(trainingFolder);

        // model has now been trained.
        String testingFile = "src/test/resources/hmmTestingDocs/random-indenture.html";
        String htmlText = Utils.readStringFromFile(testingFile);
        HtmlDocument htmlDoc = this.prepareDoc(htmlText);


        this.testNB(htmlDoc, this.nbModel);
        this.testHMM(htmlDoc, this.hmmModel);
        int count = 0;
        HashSet<String> terms = Sets.newHashSet();
        for(Paragraph paragraph : htmlDoc.getParagraphs()) {
            if (paragraph.isDefinition() && (paragraph.getDefinitions().size() > 0)) {
                terms.add(paragraph.getDefinitions().get(0));
            }
        }
        System.out.println(terms.size());
        assert (terms.size() == 106);

    }

    public HiddenMarkovModel trainHMM(String folderName) {
        HiddenMarkovModel model = new HiddenMarkovModel();

        Pipeline<String, List<String>> analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE,
                                Lists.newArrayList((Object) model))
                        .build();

        analyzer.process(folderName);
        model.updateProbabilities();;
        return model;
    }

    public BinaryNaiveBayesModel trainNB() {

        String[] trainingFolder = {
                "../Pipeline/build/resources/generated-files/not-pdef-words",
                "../Pipeline/build/resources/generated-files/pdef-words"};

        BinaryNaiveBayesModel model = new BinaryNaiveBayesModel();

        Pipeline<String, List<String>> analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_BINARY_NAIVE_BAYES_TRAINER,
                                Lists.newArrayList(model, Constants.CATEGORY_NEGATIVE))
                        .build();

        analyzer.process(trainingFolder[0]);
        analyzer =
                new Pipeline.Builder<String, List<String>>()
                        .add(Pipes.FOLDER_BINARY_NAIVE_BAYES_TRAINER,
                                Lists.newArrayList(model, Constants.CATEGORY_POSITIVE))
                        .build();

        analyzer.process(trainingFolder[1]);

        return model;
    }

    public void testNB(HtmlDocument htmlDoc, BinaryNaiveBayesModel model) {
        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .add(Pipes.HTML_DOC_BINARY_NAIVE_BAYES_TESTER,
                                Lists.newArrayList((Object) model))
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
    }


    public void testHMM(HtmlDocument htmlDoc, HiddenMarkovModel model) {
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.HTML_DOCUMENT_HIDDEN_MARKOV_MODEL_TESTING_PIPE,
                                Lists.newArrayList((Object) model, Constants.CATEGORY_POSITIVE))
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
    }

    public HtmlDocument prepareDoc(String html) {
        HtmlDocument htmlDoc = new HtmlDocument();
        htmlDoc.setSourceHtml(html);
        //create a pipeline
        Pipeline<HtmlDocument, HtmlDocument> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.PARSE_HTML_TO_DOC)
                        .add(Pipes.REMOVE_BLANK_PARAGRAPH_FROM_HTML_DOC)
                        .add(Pipes.REMOVE_NBSP_IN_HTML_DOC)
                        .add(Pipes.REPLACE_SPECIAL_QUOTE_IN_HTML_DOC)
                        .add(Pipes.TOKENIZE_PARAGRAPH_IN_HTML_DOC)
                        .build();
        htmlDoc = pipeline.process(htmlDoc);
        return htmlDoc;
    }

}