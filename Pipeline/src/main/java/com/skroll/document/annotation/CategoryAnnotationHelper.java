package com.skroll.document.annotation;

import com.google.common.base.Joiner;
import com.skroll.classifier.Category;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabhagarwal on 5/12/15.
 *  CategoryAnnotationHelper provides the functionality related to categoryAnnotation
 */
public class CategoryAnnotationHelper {
    public static final Logger logger = LoggerFactory
            .getLogger(CategoryAnnotationHelper.class);


    public static List<List<String>> getDefinedTermLists(CoreMap coreMap, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = coreMap.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return new ArrayList<>();
        if(categoryAnnotation.get(categoryId)==null) return new ArrayList<>();
        List<List<Token>> definitionList = categoryAnnotation.get(categoryId).get(CoreAnnotations.TermTokensAnnotation.class);
        List<List<String>> strings = new ArrayList<>();
        if (definitionList==null) return strings;
        for (List<Token> list: definitionList){
            strings.add(DocumentHelper.getTokenString(list));
        }
        return strings;
    }

    public static List<List<Token>> getDefinedTermTokensInParagraph(CoreMap paragraph, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return new ArrayList<>();
        if(categoryAnnotation.get(categoryId)==null) return new ArrayList<>();
        return categoryAnnotation.get(categoryId).get(CoreAnnotations.TermTokensAnnotation.class);
    }

    public static List<Paragraph> getTerm(Document document) {
        List<Paragraph> termList = new ArrayList<>();

        for (CoreMap paragraph : document.getParagraphs()) {
            for (int categoryId : Category.getCategories()) {
                        List<List<String>> definitionList = getDefinedTermLists(paragraph,categoryId );
                        for (List<String> definition : definitionList) {
                            if(logger.isTraceEnabled())
                                logger.trace( "{} \t {} \t {}",paragraph.getId(),categoryId , definition);
                            if (!definition.isEmpty()) {
                                if (!(Joiner.on(" ").join(definition).equals(""))) {
                                    termList.add(new Paragraph(paragraph.getId(), Joiner.on(" ").join(definition), categoryId));
                                }
                            }

                }
            }
        }
        return termList;
    }

    public static void displayTerm(CoreMap paragraph) {
        for (int categoryId : Category.getCategories()) {
                        List<List<String>> definitionList = getDefinedTermLists(paragraph, categoryId);
                        for (List<String> definition : definitionList) {
                            if (logger.isDebugEnabled())
                                logger.debug("{} \t {} \t {}", paragraph.getId(), categoryId, definition);
                        }
            }
    }

    public static void displayCategoryOfDoc(Document document) {
        for (CoreMap paragraph : document.getParagraphs()) {
            displayTerm(paragraph);
        }
    }
    public static boolean isCategoryId(CoreMap paragraph, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return false;
        if (categoryAnnotation.containsKey(categoryId)) {
            return true;
        }
        return false;
    }


    public static List<CoreMap> getParaWithCategoryAnnotation(Document doc, int categoryId){
        List<CoreMap> paragraphs= new ArrayList<>();
        for (CoreMap paragraph: doc.getParagraphs()){
            if (isCategoryId(paragraph, categoryId)) paragraphs.add(paragraph);
        }
        return paragraphs;
    }

    public static HashMap<Integer, CoreMap> checkNull(CoreMap paragraph,  int category) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) {
            categoryAnnotation = new HashMap<>();
        }
        CoreMap annotationCoreMap = categoryAnnotation.get(category);
        if (annotationCoreMap==null) {
            annotationCoreMap = new CoreMap();
            categoryAnnotation.put(category,annotationCoreMap);
        }
        return categoryAnnotation;
    }

    /**
     * Given a classifier Proto, classIndex, add given tokens for the category annotation in the given paragraph
     * @param paragraph
     * @param newTokens
     * @param categoryIds
     * @param classIndex
     */
    public static void addTokensForClassifier(CoreMap paragraph, List<Token> newTokens, List<Integer> categoryIds, int classIndex) {
        addDefinedTokensInCategoryAnnotation(paragraph,newTokens, categoryIds.get(classIndex));

    }
    public static void addDefinedTokensInCategoryAnnotation(CoreMap paragraph, List<Token> newTokens, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = checkNull(paragraph, categoryId);
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        List<List<Token>>  definitionList = annotationCoreMap.get(CoreAnnotations.TermTokensAnnotation.class);
        if (definitionList == null) {
            definitionList = new ArrayList<>();
            annotationCoreMap.set(CoreAnnotations.TermTokensAnnotation.class, definitionList);
        }
        definitionList.add(newTokens);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    /**
     * Given a classifier id, classIndex, set given tokens for the category annotation in the given paragraph
     * @param paragraph
     * @param definitions
     * @param categoryIds
     * @param classIndex
     */
    public static void setTokensForClassifier(CoreMap paragraph, List<List<Token>> definitions, List<Integer> categoryIds, int classIndex) {

        setDInCategoryAnnotation(paragraph,definitions, categoryIds.get(classIndex));
    }


    public static void setDInCategoryAnnotation(CoreMap paragraph, List<List<Token>> definitions, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = checkNull(paragraph, categoryId);
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        annotationCoreMap.set(CoreAnnotations.TermTokensAnnotation.class, definitions);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    public static void clearAnnotations(CoreMap paragraph){
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, null);
    }

    public static void clearCategoryAnnotation(CoreMap paragraph, int categoryId){
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return;
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        if (annotationCoreMap==null) return;
        categoryAnnotation.remove(categoryId);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    public static boolean setMatchedText(CoreMap paragraph, List<Token> selectedTerm, int categoryId) {

        List<Token> paragraphTokens = paragraph.getTokens();
        String paraTokenString = Joiner.on("").join(paragraphTokens).toLowerCase();
        String selectedTermString = Joiner.on("").join(selectedTerm).toLowerCase();

        if (!paraTokenString.contains(selectedTermString)) {
            logger.error("Existing document's paragraph {} does not contains Selected text {} ", paraTokenString,selectedTermString);
            return false;
        }


        List<Token> returnList = new ArrayList<>();
        int lastTokenPointer=0;
        int runner=0;
        int remainingSelectedTermLength = 0;
        //outer loop to find sequence of selected tokens in the para

            while (remainingSelectedTermLength < selectedTermString.length()) {
                //inner loop to find the first selectedToken in the paragraph
                for (int current = runner; current < paragraphTokens.size(); current++) {
                    int paragraphTokenLength = paragraphTokens.get(current).getText().length();
                    //check if the selected text is not the complete word or token
                    if (selectedTermString.length() < remainingSelectedTermLength + paragraphTokenLength) {
                        logger.error("One of Selected text {} does not contain the complete word [{}] ", selectedTerm, paragraphTokens.get(current).getText());
                        return false;
                    }
                    String selectedTermSubString = selectedTermString.substring(remainingSelectedTermLength, remainingSelectedTermLength + paragraphTokenLength);
                    if (paragraphTokens.get(current).getText().equalsIgnoreCase(selectedTermSubString)) {
                        remainingSelectedTermLength += paragraphTokenLength;
                        if (lastTokenPointer == 0) {
                            lastTokenPointer = current;
                        }
                        if (current > lastTokenPointer + 1) {
                            returnList.clear();
                            lastTokenPointer = current;
                            continue;
                        }
                        returnList.add(paragraphTokens.get(current));
                        lastTokenPointer = current;
                        runner++;
                        break;
                    }
                    runner++;
                }
            }
        addDefinedTokensInCategoryAnnotation(paragraph, returnList, categoryId);
        return true;

    }

    public static int getObservedClassIndex(CoreMap paragraph, List<Integer> categoryIds) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null) return 0;

        for (int index = 0; index < categoryIds.size(); index++) {
            if (categoryAnnotation.containsKey(categoryIds.get(index))) {
                return index;
            }
        }
        return 0;
    }

    public static int getObservedCategory(CoreMap paragraph,  List<Integer> categoryIds) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null) return 0;
        for (int categoryId : categoryIds) {
            if (categoryAnnotation.containsKey(categoryId)) {
                return categoryId;
            }
        }
        return 0;
    }
}
