package com.skroll.analyzer;

import com.google.common.base.CaseFormat;
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

public class TrainingAndTestingTest extends TestCase {

    public void testProcess() throws Exception {

        String trainingFolder = "src/test/resources/hmmTrainingDocs";
        trainNB();
        Trainer.trainHiddenMarkovModel(trainingFolder);

        // model has now been trained.
        //String testingFile = "src/test/resources/hmmTestingDocs/random-indenture.html";
        String testingFile = "src/test/resources/html-docs/random-10k.html";
        HtmlDocument htmlDoc = Tester.testNaiveBayes(HtmlDocumentHelper.getHtmlDocumentFromHtmlFile(testingFile));
        htmlDoc = Tester.testHiddenMarketModel(htmlDoc, Constants.CATEGORY_POSITIVE);

        int count = 0;
        HashSet<String> terms = Sets.newHashSet();
        for(Paragraph paragraph : htmlDoc.getParagraphs()) {
            if (paragraph.isDefinition() && (paragraph.getDefinitions().size() > 0)) {
                terms.add(paragraph.getDefinitions().get(0));

                // get the paragraph id
                String paraId = paragraph.getId();
                System.out.println(paragraph.getDefinitions().get(0));

                //process if definition is more than one term
                //process exceptions

                String term = paragraph.getDefinitions().get(0);
//                String upperCaseTerm =
//                        CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, paragraph.getDefinitions().get(0));
                String htmlMarkup = "<a href=\"#" + paraId + "\">" + term + "</a>";
                //search and replace in entire
                htmlDoc.setAnnotatedHtml(htmlDoc.getAnnotatedHtml().replaceAll(term, htmlMarkup));
            }
        }
        Utils.writeToFile("build/resources/test/TrainingAndTest.html", htmlDoc.getAnnotatedHtml());
        System.out.println(terms.size());
        assert (terms.size() == 162);


    }

    public void trainNB() {

        String[] trainingFolder = {
                "../Pipeline/build/resources/generated-files/not-pdef-words",
                "../Pipeline/build/resources/generated-files/pdef-words"};

        Trainer.trainBinaryNaiveBayes(trainingFolder[0], Constants.CATEGORY_NEGATIVE);
        Trainer.trainBinaryNaiveBayes(trainingFolder[1], Constants.CATEGORY_POSITIVE);

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
                                Lists.newArrayList((Object) Models.getBinaryNaiveBayesModel()))
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