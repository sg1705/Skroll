package com.skroll.util;

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

            for (CoreMap paragraph : doc.getParagraphs()) {
                if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(paragraph, Category.DEFINITION)) {
                    CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.DEFINITION, (float) 1.0);
                } else {
                    CategoryAnnotationHelper.annotateCategoryWeight(paragraph, Category.NONE, (float) 1.0);
                }
            }
            doc.setId(file.getName());
            return doc;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error reading file");
        }
        return null;
    }

    public static Document setUpTestDocForTopicModeling() {
        List<CoreMap> paraList = new ArrayList<>();

        CoreMap para;
        String text;

        para = new CoreMap();
        text = "plan benefit defined compensation plans";
        para.set(CoreAnnotations.TextAnnotation.class, text);
        para.set(CoreAnnotations.IndexInteger.class, 0);
        paraList.add(para);

        para = new CoreMap();
        text = "securities assets liabilities level";
        para.set(CoreAnnotations.TextAnnotation.class, text);
        para.set(CoreAnnotations.IndexInteger.class, 1);
        paraList.add(para);

        para = new CoreMap();
        text = "plan benefit defined compensation";
        para.set(CoreAnnotations.TextAnnotation.class, text);
        para.set(CoreAnnotations.IndexInteger.class, 2);
        paraList.add(para);

        para = new CoreMap();
        text = "plan benefit defined assets";
        para.set(CoreAnnotations.TextAnnotation.class, text);
        para.set(CoreAnnotations.IndexInteger.class, 3);
        paraList.add(para);


        Document doc = new Document();
        doc.setParagraphs(paraList);
        doc.setId("test");
        return doc;

    }


    public static Document setUpTestDoc() {
        List<CoreMap> paraList = new ArrayList<>();

        CoreMap para = new CoreMap();
        List<String> strings = Arrays.asList("\"", "in", "\"", "out", "out");
        List<Token> tokens = DocumentHelper.createTokens(strings);
        List<List<Token>> tokenList = new ArrayList<>();
        tokenList.add(tokens);
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
        para.set(CoreAnnotations.IsInTableAnnotation.class, true);
        para.set(CoreAnnotations.IdAnnotation.class, "para0");
        CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategory(para, tokenList, Category.DEFINITION);
        CategoryAnnotationHelper.annotateCategoryWeight(para, Category.DEFINITION, (float) 1.0);
        paraList.add(para);


        para = new CoreMap();
        strings = Arrays.asList("\"", "in", "\"", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out", "out");
        tokens = DocumentHelper.createTokens(strings);
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
        para.set(CoreAnnotations.IsUserObservationAnnotation.class,true);
        para.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class,true);
        para.set(CoreAnnotations.IsInTableAnnotation.class, false);
        para.set(CoreAnnotations.IdAnnotation.class, "para1");
        CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategory(para, tokenList, Category.DEFINITION);
        CategoryAnnotationHelper.annotateCategoryWeight(para, Category.DEFINITION, (float) 1.0);
        paraList.add(para);

        para = new CoreMap();
        strings = Arrays.asList("a", "b", "c");
        tokens = DocumentHelper.createTokens(strings);
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
        para.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        para.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, true);
        para.set(CoreAnnotations.IsInTableAnnotation.class, true);
        para.set(CoreAnnotations.IdAnnotation.class, "para2");
        CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategory(para, tokenList, Category.NONE);
        CategoryAnnotationHelper.annotateCategoryWeight(para, Category.NONE, (float) 1.0);
        paraList.add(para);

        Document doc = new Document();
        doc.setParagraphs(paraList);
        doc.setId("test");
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
