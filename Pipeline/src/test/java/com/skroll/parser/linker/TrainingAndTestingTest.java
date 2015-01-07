package com.skroll.parser.linker;

import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.skroll.analyzer.evaluate.Tester;
import com.skroll.analyzer.train.Trainer;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.parser.Parser;
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

        int defCount = 0;
        for(CoreMap paragraph : htmlDoc.getParagraphs()) {
            if (DocumentHelper.isDefinition(paragraph)) {
                defCount++;
                System.out.println(paragraph.getText());
            }
        }
        System.out.println(defCount);

        htmlDoc = Tester.testHiddenMarketModel(htmlDoc, Constants.CATEGORY_POSITIVE);

        int count = 0;
        HashSet<String> terms = Sets.newHashSet();
        for(CoreMap paragraph : htmlDoc.getParagraphs()) {
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

}