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
    public static List<Paragraph> getParagraphsAnnotatedWithAnyCategory(Document document) {
        List<Paragraph> termList = new ArrayList<>();
        for (CoreMap paragraph : document.getParagraphs()) {
            for (int categoryId : Category.getCategories()) {
                List<List<String>> definitionList = getTokenStringsForCategory(paragraph, categoryId);
                for (List<String> definition : definitionList) {
                    if(logger.isTraceEnabled())
                        logger.trace( "{} \t {} \t {}",paragraph.getId(),categoryId , definition);
                    if (!definition.isEmpty()) {
                        if (!(Joiner.on(" ").join(definition).equals(""))) {
                            boolean isObserved = DocumentHelper.isObserved(paragraph);
                            termList.add(new Paragraph(paragraph.getId(), Joiner.on(" ").join(definition), categoryId, isObserved));
                        }
                    }
                }
            }
        }
        return termList;
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
        for (int categoryId : Category.getCategories()) {
                        List<List<String>> definitionList = getTokenStringsForCategory(paragraph, categoryId);
                        for (List<String> definition : definitionList) {
                            if (logger.isDebugEnabled())
                                logger.debug("{} \t {} \t {}", paragraph.getId(), categoryId, definition);
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
     * clear category annotation of a given paragraph and a given category
     * @param paragraph
     * @param categoryId
     */
    public static void clearCategoryAnnotations(CoreMap paragraph, int categoryId){
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
     * @param selectedTerm
     * @param categoryId
     * @return
     */
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
        annotateParagraphWithTokensAndCategory(paragraph, returnList, categoryId);
        return true;

    }

    /**
     * For a given paragraph, get the index in the list of categoryIds. This index will be used by Model as classIndex.
     * It will return -1 if there in no category annotation
     * @param paragraph
     * @param categoryIds
     * @return Index in list
     */
    public static int getObservedClassIndex(CoreMap paragraph, List<Integer> categoryIds) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null) return -1;

        for (int index = 0; index < categoryIds.size(); index++) {
            if (categoryAnnotation.containsKey(categoryIds.get(index))) {
                return index;
            }
        }
        return -1;
    }

    /**
     * For a given paragraph, get the categoryId. It must be in the given list of categoryIds.
     * @param paragraph
     * @param categoryIds
     * @return
     */
    public static int getObservedCategoryId(CoreMap paragraph, List<Integer> categoryIds) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null) return 0;
        for (int categoryId : categoryIds) {
            if (categoryAnnotation.containsKey(categoryId)) {
                return categoryId;
            }
        }
        return 0;
    }

    /**
     * For a given paragraph, for each categories, copy the current category weight into the prior category weight.
     * @param paragraph
     */
    public static void copyCurrentCategoryWeightsToPrior(CoreMap paragraph){
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return;
        for (int categoryId : Category.getCategories()) {
            CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
            if(annotationCoreMap!=null){
                float currentWeight = annotationCoreMap.get(CoreAnnotations.CurrentCategoryWeightFloat.class);
                annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, currentWeight);
                logger.debug("{} \t {} \t {}", paragraph.getId(), categoryId, currentWeight);
            }
        }
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
     * Populate training weight of model using the training weights of categories of a paragraph into array of double that will feed to model.
     * @param paragraph
     * @param categoryIds
     * @return trainingWeights
     */
    public static double[][] populateTrainingWeights(CoreMap paragraph, List<Integer> categoryIds) {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return null;
        int ObservedCategory = CategoryAnnotationHelper.getObservedCategoryId(paragraph, categoryIds);
        int ObservedClassIndex = CategoryAnnotationHelper.getObservedClassIndex(paragraph, categoryIds);

        CoreMap annotationCoreMap = categoryAnnotation.get(ObservedCategory);
        if(annotationCoreMap==null) return null;

        double[][] trainingWeights = new double[2][categoryIds.size()];

        trainingWeights[0][ObservedClassIndex] = annotationCoreMap.get(CoreAnnotations.PriorCategoryWeightFloat.class);
        trainingWeights[1][ObservedClassIndex] = annotationCoreMap.get(CoreAnnotations.CurrentCategoryWeightFloat.class);
        if (logger.isWarnEnabled()) {
            for (double[] w1 : trainingWeights) {
                for (double w2 : w1)
                    logger.trace("trainingWeights: {}", w2);
            }
        }
        return trainingWeights;
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
                annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, (float)0);
                logger.debug("Cleared \t {} \t {}", paragraph.getId(), categoryId);
            }
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
