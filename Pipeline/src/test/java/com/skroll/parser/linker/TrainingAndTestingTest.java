package com.skroll.parser.linker;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.skroll.analyzer.evaluate.Tester;
import com.skroll.analyzer.model.Models;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.analyzer.train.Trainer;
import com.skroll.document.*;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.io.File;
import java.util.HashSet;

public class TrainingAndTestingTest extends TestCase {

    public void testProcess() throws Exception {

        String trainingFolder = "src/test/resources/analyzer/hmmTrainingDocs";
        trainNB();
        Trainer.trainHiddenMarkovModel(trainingFolder);

        // document has now been trained.
        //String testingFile = "src/test/resources/parser.analyzer.hmmTestingDocs/random-indenture.html";
        String testingFile = "src/test/resources/html-docs/random-indenture.html";
        Document htmlDoc = Tester.testNaiveBayes(Parser.parseDocumentFromHtmlFile(testingFile));
        htmlDoc = Tester.testHiddenMarketModel(htmlDoc, Constants.CATEGORY_POSITIVE);

        int count = 0;
        HashSet<String> terms = Sets.newHashSet();
        for(Entity paragraph : htmlDoc.getParagraphs()) {
            if (DocumentHelper.isDefinition(paragraph) && (DocumentHelper.getDefinedTerms(paragraph).size() > 0)) {
                terms.add(DocumentHelper.getDefinedTerms(paragraph).get(0));
                        //paragraph.getDefinitions().get(0));

                // get the paragraph id
                String paraId = paragraph.getId();
                System.out.println(DocumentHelper.getDefinedTerms(paragraph).get(0));

                //process if definition is more than one term
                //process exceptions

                String term = DocumentHelper.getDefinedTerms(paragraph).get(0);
//                String upperCaseTerm =
//                        CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, paragraph.getDefinitions().get(0));
                String htmlMarkup = "<a href=\"#" + paraId + "\">" + term + "</a>";
                //search and replace in entire
                htmlDoc.setTarget(htmlDoc.getTarget().replaceAll(term, htmlMarkup));
            }
        }
        Files.createParentDirs(new File("build/resources/test/parser/linker/TrainingAndTest.html"));
        Utils.writeToFile("build/resources/test/parser/linker/TrainingAndTest.html", htmlDoc.getTarget());
        System.out.println(terms.size());
        assert (terms.size() == 160);


    }

    public void trainNB() {

        String[] trainingFolder = {
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/not-pdef-words",
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/pdef-words"};

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