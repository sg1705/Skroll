package com.skroll.document.annotation;

import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * DocTypeAnnotationHelper provides the functionality related to categoryAnnotation at the document level.
 * These doc level category annotation are used to store the doc type, user observation and weight for those doc type annotation.
 * Created by saurabhagarwal on 5/12/15.
 *
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
     * For a document, from the document level annotation, get the weight of docType.
     * @param document
     * @return weight
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
}
