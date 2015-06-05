package com.skroll.document.annotation;

import com.google.common.base.Joiner;
import com.skroll.classifier.Category;
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

    /*
    public static List<String> getTexts(CoreMap coreMap, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = coreMap.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return new ArrayList<>();
        if(categoryAnnotation.get(categoryId)==null) return new ArrayList<>();
        return DocumentHelper.getTokenString(categoryAnnotation.get(categoryId).getTokens());
    }

    public static List<Token> getTokensInParagraph(CoreMap paragraph, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return new ArrayList<>();
        if(categoryAnnotation.get(categoryId)==null) return new ArrayList<>();
        return categoryAnnotation.get(categoryId).getTokens();
    }
    */
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
   /*
    public static void addTokensInCategoryAnnotation(CoreMap paragraph, List<Token> newTokens,  int category) {
        HashMap<Integer, CoreMap> categoryAnnotation = checkNull(paragraph, category);
        CoreMap annotationCoreMap = categoryAnnotation.get(category);
        List<Token> tokens = annotationCoreMap.getTokens();
        if (tokens == null) {
                tokens = new ArrayList<>();
        }
            tokens.addAll(newTokens);
            annotationCoreMap.set(CoreAnnotations.TokenAnnotation.class, tokens);
            paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
        }
*/
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

    public static void setDInCategoryAnnotation(CoreMap paragraph, List<List<Token>> definitions, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = checkNull(paragraph, categoryId);
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        annotationCoreMap.set(CoreAnnotations.TermTokensAnnotation.class, definitions);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

   /*
    public static void setCategoryAnnotation(CoreMap paragraph, List<Token> tokens ,int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = checkNull(paragraph, categoryId);
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        annotationCoreMap.set(CoreAnnotations.TokenAnnotation.class, tokens);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }
   */
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

    public static boolean setMatchedText(CoreMap coreMap, List<Token> addedTerm, int categoryId) {

        List<Token> tokenList = coreMap.getTokens();
        String tokenStringList = Joiner.on("").join(tokenList);
        String addedTermList = Joiner.on("").join(addedTerm);

        if (!tokenStringList.contains(addedTermList)) {
            return false;
        }

        List<Token> returnList = new ArrayList<>();
        int j=0;
        int l=0;
        int remainingAddedTermLength = 0;

        while (remainingAddedTermLength < addedTermList.length()) {
            for(int i=l; i<tokenList.size(); i++){
                int tokenLength = tokenList.get(i).getText().length();
                //check if the selected text is not the complete word or token
                if(addedTermList.length() < remainingAddedTermLength + tokenLength ){
                    logger.error("One of Selected text {} does not contain the complete word [{}] ", addedTerm,tokenList.get(i).getText());
                    return false;
                }
                String addedTermSubString = addedTermList.substring(remainingAddedTermLength,remainingAddedTermLength + tokenLength);
                if(tokenList.get(i).getText().equals(addedTermSubString)) {
                    remainingAddedTermLength+=tokenLength;
                    if (j==0) {
                        j=i;
                    }
                    if (i>j+1) {
                        returnList.clear();
                        j=i;
                        continue;
                    }
                    returnList.add(tokenList.get(i));
                    j=i;
                    l++;
                    break;
                }
                l++;
            }
        }
            addDefinedTokensInCategoryAnnotation(coreMap, returnList, categoryId);

        return true;

    }
}
