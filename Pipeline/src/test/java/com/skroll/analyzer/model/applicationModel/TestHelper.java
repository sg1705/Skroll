package com.skroll.analyzer.model.applicationModel;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/16/15.
 */
public class TestHelper {
    static String trainingFolderName = "src/test/resources/analyzer/definedTermExtractionTraining";
    static String trainingFileName = "src/test/resources/analyzer/definedTermExtractionTraining/AMC Networks CA.html";

    public static Document makeTrainingDoc(File file) {
        String htmlString = null;
        try {
            htmlString = Utils.readStringFromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }

        try {
            Document htmlDoc = new Document();
            htmlDoc = Parser.parseDocumentFromHtml(htmlString);
            //create a pipeline

            Pipeline<Document, Document> pipeline =
                    new Pipeline.Builder()
                            .add(Pipes.EXTRACT_DEFINITION_FROM_PARAGRAPH_IN_HTML_DOC)
                            .build();
            Document doc = pipeline.process(htmlDoc);
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }
        return null;
    }

    public static Document makeDoc(File file) {
        String htmlString = null;
        try {
            htmlString = Utils.readStringFromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }

        try {
            Document htmlDoc = new Document();
            htmlDoc = Parser.parseDocumentFromHtml(htmlString);

            return htmlDoc;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }
        return null;
    }

    public static Document setUpTestDoc() {
        List<CoreMap> paraList = new ArrayList<>();

        CoreMap para = new CoreMap();
        List<String> strings = Arrays.asList("\"", "in", "\"", "out", "out");
        List<Token> tokens = DocumentHelper.createTokens(strings);
        List<List<Token>> tokenList = new ArrayList<>();
        tokenList.add(tokens);
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
        CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategory(para, tokenList, Category.DEFINITION);
        paraList.add(para);


        para = new CoreMap();
        strings = Arrays.asList("\"", "in", "\"", "out", "out");
        tokens = DocumentHelper.createTokens(strings);
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
        CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategory(para, tokenList, Category.DEFINITION);
        paraList.add(para);

        Document doc = new Document();
        doc.setParagraphs(paraList);
        return doc;

    }

    public static boolean compareRVList(List<RandomVariable> list, List<RandomVariable> list2) {
        if (list.size() != list2.size()) {
            return false;
        }
        boolean isEqual = true;
        for(int ii = 0; ii < list.size(); ii++) {
            isEqual = isEqual && list.get(ii).equals(list2.get(ii));
        }
        return isEqual;
    }
}
