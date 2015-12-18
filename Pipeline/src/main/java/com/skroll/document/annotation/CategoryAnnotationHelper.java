package com.skroll.document.annotation;

import com.google.common.base.Joiner;
import com.skroll.classifier.Category;
import com.skroll.document.*;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by saurabhagarwal on 5/12/15.
 *  CategoryAnnotationHelper provides the functionality related to categoryAnnotation
 */
public class CategoryAnnotationHelper {
    public static final Logger logger = LoggerFactory
            .getLogger(CategoryAnnotationHelper.class);

    /**
     * For a paragraph, get all Token Strings that are annotated with the given categoryId
     * @param paragraph
     * @param categoryId
     * @return List of List of Token String
     */
    public static List<List<String>> getTokenStringsForCategory(CoreMap paragraph, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
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

    /**
     * For a given paragraph, get all tokens that are annotated with the given categoryId
     * @param paragraph
     * @param categoryId
     * @return List of List of Token
     */
    public static List<List<Token>> getTokensForCategory(CoreMap paragraph, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return new ArrayList<>();
        if(categoryAnnotation.get(categoryId)==null) return new ArrayList<>();
        return categoryAnnotation.get(categoryId).get(CoreAnnotations.TermTokensAnnotation.class);
    }

    /**
     * Get paragraph Objects that are annotated with one or more categories
     * @param document
     * @return List of Paragragh
     */
    public static List<TermProto> getParagraphsAnnotatedWithAnyCategory(Document document) {
        List<TermProto> termProto = new ArrayList<>();
        Map<Integer, Set<String>> check = new HashMap<>();
            for (int categoryId : Category.getCategoriesExcludingNONE()) {
                check.put(categoryId, new HashSet<>());
            }
        for (CoreMap paragraph : document.getParagraphs()) {
            for (int categoryId : Category.getCategoriesExcludingNONE()) {
                List<List<String>> terms = getTokenStringsForCategory(paragraph, categoryId);
                for (List<String> term : terms) {
                    if(logger.isTraceEnabled())
                        logger.trace("{} \t {} \t {}", paragraph.getId(), categoryId, term);
                    if (!term.isEmpty()) {
                        String termString = Joiner.on(" ").join(term);
                        if (!(termString.equals(""))) {
                            if (!check.get(categoryId).contains(termString)) {
                                boolean isObserved = DocumentHelper.isObserved(paragraph);
                                termProto.add(new TermProto(paragraph.getId(), termString, categoryId, isObserved));
                                check.get(categoryId).add(termString);
                            }
                        }
                    }
                }
            }
        }
        return termProto;
    }

    /**
     * Get all paragraphs (coreMap) those are annotated with the given category id.
     * @param doc
     * @param categoryId
     * @return list of paragraphs
     */
    public static List<CoreMap> getParagraphsAnnotatedWithCategory(Document doc, int categoryId){
        List<CoreMap> paragraphs= new ArrayList<>();
        for (CoreMap paragraph: doc.getParagraphs()){
            if (isParagraphAnnotatedWithCategoryId(paragraph, categoryId)) paragraphs.add(paragraph);
        }
        return paragraphs;
    }
    /**
     * Display the paragraphs those are annotated with any category
     * @param paragraph
     */
    public static void displayParagraphsAnnotatedWithAnyCategory(CoreMap paragraph) {
        for (int categoryId : Category.getCategoriesExcludingNONE()) {
                        List<List<String>> tokenList = getTokenStringsForCategory(paragraph, categoryId);
                        for (List<String> token : tokenList) {
                            if (logger.isDebugEnabled())
                                logger.debug("{} \t {} \t {}", paragraph.getId(), categoryId, token);
                        }
            }
    }

    /**
     * Display all paragraphs those are annotated with category in a document
     * @param document
     */
    public static void displayParagraphsAnnotatedWithAnyCategoryInDoc(Document document) {
        for (CoreMap paragraph : document.getParagraphs()) {
            displayParagraphsAnnotatedWithAnyCategory(paragraph);
        }
    }

    /**
     * To find whether a given paragraph annotated with given category or not.
     * @param paragraph
     * @param categoryId
     * @return
     */
    public static boolean isParagraphAnnotatedWithCategoryId(CoreMap paragraph, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return false;
        if (categoryAnnotation.containsKey(categoryId)) {
            return true;
        }
        return false;
    }


    /**
     * create Category Annotation object and coremap object for a given category
     * if it does not exist for a given paragraph and given category
     * @param paragraph
     * @param category
     * @return categoryAnnotation
     */
    private static HashMap<Integer, CoreMap> createOrGetCategoryAnnotation(CoreMap paragraph, int category) {
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
     * Annotated a given Paragraph with given List of token and a given categoryId
     * @param paragraph
     * @param newTokens
     * @param categoryId
     */
    public static void annotateParagraphWithTokensAndCategory(CoreMap paragraph, List<Token> newTokens, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = createOrGetCategoryAnnotation(paragraph, categoryId);
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        List<List<Token>>  tokens = annotationCoreMap.get(CoreAnnotations.TermTokensAnnotation.class);
        if (tokens == null) {
            tokens = new ArrayList<>();
            annotationCoreMap.set(CoreAnnotations.TermTokensAnnotation.class, tokens);
        }
        tokens.add(newTokens);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    /**
     * Annotated a given Paragraph with given List of List of token and a given categoryId
     * @param paragraph
     * @param tokens
     * @param categoryId
     */
    public static void annotateParagraphWithTokensListAndCategory(CoreMap paragraph, List<List<Token>> tokens, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = createOrGetCategoryAnnotation(paragraph, categoryId);
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        annotationCoreMap.set(CoreAnnotations.TermTokensAnnotation.class, tokens);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    /**
     * Annotated a given Paragraph with given List of token and category. The category will be determined by the index  of the categoryIds list
     * This function is used by the model.
     * @param paragraph
     * @param newTokens
     * @param categoryIds
     * @param classIndex
     */
    public static void annotateParagraphWithTokensAndCategoryOfClassIndex(CoreMap paragraph, List<Token> newTokens, List<Integer> categoryIds, int classIndex) {
        // No weight will be set as this function will be used to infer the categories
        annotateParagraphWithTokensAndCategory(paragraph, newTokens, categoryIds.get(classIndex));
    }

    /**
     * Annotated a given Paragraph with given List of List of token and category. The category will be determined by the index  of the categoryIds list
     * This function is used by the model.
     *  @param paragraph
     * @param definitions
     * @param categoryIds
     * @param classIndex
     */
    public static void annotateParagraphWithTokensListAndCategoryOfClassIndex(CoreMap paragraph, List<List<Token>> definitions, List<Integer> categoryIds, int classIndex) {
        // No weight will be set as this function will be used to infer the categories
        annotateParagraphWithTokensListAndCategory(paragraph, definitions, categoryIds.get(classIndex));
    }

    /**
     * clear category annotation of a given paragraph
     * @param paragraph
     */
    public static void clearCategoryAnnotations(CoreMap paragraph){
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, null);
    }

    /**
     * Removes observations in the paragraph
     * @param paragraph
     */
    public static void clearObservations(CoreMap paragraph) {
        CategoryAnnotationHelper.clearCategoryAnnotations(paragraph);
        paragraph.remove(CoreAnnotations.IsUserObservationAnnotation.class);
        paragraph.remove(CoreAnnotations.TrainingWeightAnnotationFloat.class);
    }



    public static void clearCategoryAnnotation(CoreMap paragraph, int categoryId){
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return;
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        if (annotationCoreMap==null) return;
        categoryAnnotation.remove(categoryId);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    /**
     * Match the text of the given list of selected tokens with the text in the paragraphs. If the text is matched,
     * annotate Paragraph With Tokens And Category
     * @param paragraph
     * @param selectedTermString
     * @param categoryId
     * @return
     */
    public static boolean setMatchedText(CoreMap paragraph, String selectedTermString, int categoryId) {
        // instead of matching for selected token, we are taking the whole para as TOC
        annotateParagraphWithTokensAndCategory(paragraph, paragraph.getTokens(), categoryId);
        return true;

        /*
        List<Token> paragraphTokens = paragraph.getTokens();
        String paraTokenString = paragraph.getText(); // Joiner.on("").join(paragraphTokens).toLowerCase();
        //String selectedTermString = Joiner.on("").join(selectedTerm).toLowerCase();

        if (!paraTokenString.contains(selectedTermString)) {
            logger.error("Existing document's paragraph {} does not contains Selected text {} ", paraTokenString,selectedTermString);
            return false;
        }
        if (categoryId == Category.USER_TOC) {
            annotateParagraphWithTokensAndCategory(paragraph, paragraph.getTokens(), categoryId);
            return true;
        }
        if (!paraTokenString.equals(selectedTermString)) {
            String[] origWords = paraTokenString.split(" ");
            String[] compareWords =selectedTermString.split(" ");

            for (int firstOrgWordsCounter = 0; firstOrgWordsCounter < origWords.length; firstOrgWordsCounter++) {
                if (origWords[firstOrgWordsCounter].equals(compareWords[0])) {
                    int j;
                    for (j = 0; j < compareWords.length; j++) {
                        if (!origWords[firstOrgWordsCounter + j].equals(compareWords[j])) {
                            break;
                        }
                    }
                    if (j == compareWords.length) {
                        annotateParagraphWithTextAndCategory(paragraph, selectedTermString, categoryId);
                        return true;
                    }
                }
            }
            return false;
        }
        annotateParagraphWithTextAndCategory(paragraph, selectedTermString, categoryId);
        return true;
        */
    }

    /**
     * Annotated a given Paragraph with given words and a given categoryId
     * @param paragraph
     * @param words
     * @param categoryId
     */
    public static void annotateParagraphWithTextAndCategory(CoreMap paragraph, String words, int categoryId) {
        List<Token> tokens = null;
        try {
            Document tempDoc = Parser.parseDocumentFromHtml(words);
            tokens = DocumentHelper.getTokensOfADoc(tempDoc);
        } catch (ParserException e) {
            e.printStackTrace();
        }
        annotateParagraphWithTokensAndCategory(paragraph, tokens, categoryId);
    }

    /**
     * For a given paragraph, for each categories, copy the current category weight into the prior category weight.
     * @param paragraph
     */
    public static void copyCurrentCategoryWeightsToPrior(CoreMap paragraph,List<Integer> categories){
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return;
        for (int categoryId : categories) {
            CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
            if(annotationCoreMap!=null){
                float currentWeight = annotationCoreMap.get(CoreAnnotations.CurrentCategoryWeightFloat.class);
                annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, currentWeight);
                logger.debug("{} \t {} \t {}", paragraph.getId(), categoryId, currentWeight);
            }
        }
    }

    /**
     * For a given paragraph, for each categories, copy the current category weight into the prior category weight.
     * @param paragraph
     */
    public static void copyCurrentCategoryWeightsToPrior(CoreMap paragraph){
        copyCurrentCategoryWeightsToPrior(paragraph,Category.getCategories());
    }

    /**
     * For a given paragraph, for each categories, copy the current category weight into the prior category weight.
     * @param paragraph
     */
    public static void copyCurrentCategoryWeightsToPriorForDocType(CoreMap paragraph){
        copyCurrentCategoryWeightsToPrior(paragraph,Category.getDocType());
    }

    /**
     * Annotate the CategoryWeight for given current and prior category for a given paragraph
     * @param paragraph
     * @param categoryId
     * @param currentCategoryWeight
     * @param priorCategoryWeight
     */
    public static void annotateCategoryWeight(CoreMap paragraph, int categoryId, float currentCategoryWeight, float priorCategoryWeight){
        HashMap<Integer, CoreMap> categoryAnnotation = createOrGetCategoryAnnotation(paragraph, categoryId);
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        annotationCoreMap.set(CoreAnnotations.CurrentCategoryWeightFloat.class, currentCategoryWeight);
        annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, priorCategoryWeight);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    /**
     * Annotate the CategoryWeight for given current category for a given paragraph
     * @param paragraph
     * @param categoryId
     * @param currentCategoryWeight
     */
    public static void annotateCategoryWeight(CoreMap paragraph, int categoryId, float currentCategoryWeight){
        HashMap<Integer, CoreMap> categoryAnnotation = createOrGetCategoryAnnotation(paragraph, categoryId);
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        annotationCoreMap.set(CoreAnnotations.CurrentCategoryWeightFloat.class, currentCategoryWeight);
        annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, 0f);
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    /**
     * Clear Prior training weight of all categories for a paragraph
     * @param paragraph
     */
    public static void clearPriorCategoryWeight(CoreMap paragraph){
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return;
        for (int categoryId : Category.getCategories()) {
            CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
            if(annotationCoreMap!=null){
                annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, 0f);
                logger.debug("Cleared \t {} \t {}", paragraph.getId(), categoryId);
            }
        }

    }

    /**
     * Copy annotations from one CoreMap into another
     *
     * @param copyFrom CoreMap to copy from
     * @param copyInto
     */
    public static void copyAnnotations(CoreMap copyFrom, CoreMap copyInto) {
        if (copyFrom.containsKey(CoreAnnotations.CategoryAnnotations.class)) {
            HashMap classId = copyFrom.get(CoreAnnotations.CategoryAnnotations.class);
            copyInto.set(CoreAnnotations.CategoryAnnotations.class, classId);
        }

        if (copyFrom.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
            boolean userObservation = copyFrom.get(CoreAnnotations.IsUserObservationAnnotation.class);
            copyInto.set(CoreAnnotations.IsUserObservationAnnotation.class, userObservation);
        }

        if (copyFrom.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
            boolean feedback = copyFrom.get(CoreAnnotations.IsTrainerFeedbackAnnotation.class);
            copyInto.set(CoreAnnotations.IsTrainerFeedbackAnnotation.class, feedback);
        }

        if (copyFrom.containsKey(CoreAnnotations.SearchIndexAnnotation.class)) {
            String indexes = copyFrom.get(CoreAnnotations.SearchIndexAnnotation.class);
            copyInto.set(CoreAnnotations.SearchIndexAnnotation.class, indexes);
        }
    }




    /**
     * Get the categoryAnnotation core map.
     * @param paragraph
     * @param categoryId
     * @return
     */
    public static CoreMap getCategoryAnnotation(CoreMap paragraph, int categoryId) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return null;
        CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
        if(annotationCoreMap==null) return null;
        return annotationCoreMap;
    }

}
