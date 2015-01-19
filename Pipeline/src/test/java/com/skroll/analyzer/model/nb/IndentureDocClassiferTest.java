package com.skroll.analyzer.model.nb;

import com.google.common.io.Files;
import com.skroll.analyzer.evaluate.Tester;
import com.skroll.analyzer.train.Trainer;
import com.skroll.parser.Parser;
import com.skroll.pipeline.util.Constants;
import junit.framework.TestCase;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;

public class IndentureDocClassiferTest extends TestCase {

    @Test
    public void testIndentureClassification() throws Exception {
        //train the model
        String DOC_POSITIVE_FOLDER = "src/test/resources/analyzer/train/docclassifier/pdef-words";
        String DOC_NEGATIVE_FOLDER = "src/test/resources/analyzer/train/docclassifier/not-pdef-words";

        Trainer.trainBinaryNaiveBayes(DOC_POSITIVE_FOLDER, Constants.CATEGORY_POSITIVE);
        Trainer.trainBinaryNaiveBayes(DOC_NEGATIVE_FOLDER, Constants.CATEGORY_POSITIVE);

        String INPUT_FOLDER = "src/test/resources/analyzer/evaluate/docclassifier/";

        Iterator<File> files = Files.fileTreeTraverser().children(new File(INPUT_FOLDER)).iterator();
        while (files.hasNext()) {
            File file = files.next();
            String fileName = INPUT_FOLDER + file.getName();

            double prob = Tester.testDocclassifier(Parser.parseDocumentFromHtmlFile(fileName));
            System.out.println(fileName + "[" + prob + "]");
        }
    }

}