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

    /**
     * This test does the following.
     *
     * 1. Trains both models.
     * 2. Evaluates a document.
     * 3. Identifies definitions.
     * 4. Links them back into the document

     * @throws Exception
     */
    public void testProcess() throws Exception {

        //Step 1 - Train the models
        String trainingFolder = "src/test/resources/analyzer/hmmTrainingDocs";
        trainNB();
        Trainer.trainHiddenMarkovModel(trainingFolder);

        // document has now been trained.

        // Step 2 - Evaluate a random document
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

        // Step 3 - Identify definitions
        int count = 0;
        HashSet<String> terms = Sets.newHashSet();
        for(CoreMap paragraph : htmlDoc.getParagraphs()) {
            if (DocumentHelper.isDefinition(paragraph) && (DocumentHelper.getDefinedTerms(paragraph).size() > 0)) {
                terms.add(DocumentHelper.getDefinedTerms(paragraph).get(0));

                // get the paragraph id
                String paraId = paragraph.getId();
                System.out.println(DocumentHelper.getDefinedTerms(paragraph).get(0));

                //process if definition is more than one term
                //process exceptions

                // Step 4 - Link back to the document
                String term = DocumentHelper.getDefinedTerms(paragraph).get(0);
                String htmlMarkup = "<a href=\"#" + paraId + "\">" + term + "</a>";
                //search and replace in entire
                htmlDoc.setTarget(htmlDoc.getTarget().replaceAll(term, htmlMarkup));
            }
        }
        Files.createParentDirs(new File("build/resources/test/parser/linker/TrainingAndTest.html"));
        Utils.writeToFile("build/resources/test/parser/linker/TrainingAndTest.html", htmlDoc.getTarget());
        System.out.println(terms.size());
        assert (terms.size() == 161);

    }

    public static void trainNB() {

        String[] trainingFolder = {
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/not-pdef-words",
                "src/test/resources/analyzer/train/FolderBinaryNaiveBayesTrainerPipeTest/pdef-words"};

        Trainer.trainBinaryNaiveBayes(trainingFolder[0], Constants.CATEGORY_NEGATIVE);
        Trainer.trainBinaryNaiveBayes(trainingFolder[1], Constants.CATEGORY_POSITIVE);

    }

}