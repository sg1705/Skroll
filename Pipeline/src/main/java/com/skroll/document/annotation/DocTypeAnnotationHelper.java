package com.skroll.document.annotation;

import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * Created by saurabhagarwal on 5/12/15.
 *  CategoryAnnotationHelper provides the functionality related to categoryAnnotation
 */
public class DocTypeAnnotationHelper {
    public static final Logger logger = LoggerFactory
            .getLogger(DocTypeAnnotationHelper.class);

    /**
     * For a document, from the document level annotation, get the docType.
     * @param document
     * @return List of List of Token String
     */
    public static int getDocType(CoreMap document) {
        HashMap<Integer, CoreMap> categoryAnnotation = document.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null)
            return Category.DOCTYPE_NONE;
        if (categoryAnnotation.keySet().iterator().hasNext()) {
            return categoryAnnotation.keySet().iterator().next();
        }
        return Category.DOCTYPE_NONE;
    }


    /**
     * For a document, from the document level annotation, get the docType.
     * @param document
     * @return List of List of Token String
     */
    public static float getDocTypeTrainingWeight(CoreMap document) {
        HashMap<Integer, CoreMap> categoryAnnotation = document.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return 0f;
        if (categoryAnnotation.keySet().iterator().hasNext()) {
            int doctype = categoryAnnotation.keySet().iterator().next();
            CoreMap annotationCoreMap = categoryAnnotation.get(doctype);
            if (annotationCoreMap!=null){
                if(annotationCoreMap.size()!=0) {
                    float currentWeight = annotationCoreMap.get(CoreAnnotations.CurrentCategoryWeightFloat.class);
                    return currentWeight;
                }
            }
        }
        return 0f;
    }

    /**
     * To find whether a given paragraph annotated with given category or not.
     * @param paragraph
     * @return
     */
    public static int extractDocTypeFromSingleParaDocument (CoreMap paragraph) throws Exception {
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) return Category.DOCTYPE_NONE;
        if (categoryAnnotation.keySet().iterator().hasNext()) {
            return categoryAnnotation.keySet().iterator().next();
        }
        return Category.DOCTYPE_NONE;
    }


    /**
     * Annotated a given Paragraph with given List of token and a given categoryId
     * @param doc
     * @param docType
     */
    public static void annotateDocTypeWithWeightAndUserObservation(Document doc,int docType, float currentCategoryWeight) {
        HashMap<Integer, CoreMap> categoryAnnotation = new HashMap<>();
        CoreMap annotationCoreMap  = new CoreMap();
        annotationCoreMap.set(CoreAnnotations.CurrentCategoryWeightFloat.class, currentCategoryWeight);
        annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, 0f);
        categoryAnnotation.put(docType,annotationCoreMap);
        doc.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
        doc.set(CoreAnnotations.IsUserObservationAnnotation.class,true);
    }

    /**
     * Annotated a given Paragraph with given List of token and a given categoryId
     * @param doc
     * @param docType
     */
    public static void annotateDocType(Document doc,int docType) {
        HashMap<Integer, CoreMap> categoryAnnotation = new HashMap<>();
        CoreMap annotationCoreMap  = new CoreMap();
        categoryAnnotation.put(docType,annotationCoreMap);
        doc.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
    }

    /**
     * clear category annotation of a given document
     * @param document
     */
    public static void clearCategoryAnnotations(Document document){
        document.set(CoreAnnotations.CategoryAnnotations.class, null);
    }

    /**
     * Removes observations in the paragraph
     * @param document
     */
    public static void clearObservations(Document document) {
        DocTypeAnnotationHelper.clearCategoryAnnotations(document);
        document.remove(CoreAnnotations.IsUserObservationAnnotation.class);
        document.remove(CoreAnnotations.TrainingWeightAnnotationFloat.class);
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
