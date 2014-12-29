package com.skroll.analyzer.evaluate;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.model.HtmlDocument;
import com.skroll.model.Paragraph;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import com.skroll.pipeline.util.Utils;
import junit.framework.TestCase;

import java.util.List;

public class HtmlDocumentNaiveBayesTesterTest extends TestCase {

    public void testProcess() throws Exception {
        String[] trainingFolder = {
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/not-pdef-words",
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/pdef-words"};
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


        // read a sample file
        String fileName = "src/test/resources/analyzer/html-docs/random-indenture.html";
        String htmlString = Utils.readStringFromFile(fileName);

        HtmlDocument htmlDoc= new HtmlDocument();
        htmlDoc.setSourceHtml(htmlString);

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
        HtmlDocument doc = pipeline.process(htmlDoc);
        int defCount = 0;
        for(Paragraph paragraph : htmlDoc.getParagraphs()) {
            if (paragraph.isDefinition()) {
                defCount++;
                System.out.println(paragraph.getText());
            }
        }
        System.out.println(defCount);
        assert ( defCount == 174);
    }
}