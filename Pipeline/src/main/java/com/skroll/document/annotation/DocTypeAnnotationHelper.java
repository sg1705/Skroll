package com.skroll.document.annotation;

import com.skroll.classifier.Category;
import com.skroll.classifier.Classifier;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.factory.DocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

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
     *
     * @param document
     * @return List of List of Token String
     */
    public static int getDocType(CoreMap document) {
        HashMap<Integer, CoreMap> categoryAnnotation = document.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null)
            return Category.DOCTYPE_NONE;
        if (categoryAnnotation.keySet().iterator().hasNext()) {
            return categoryAnnotation.keySet().iterator().next();
        }
        return Category.DOCTYPE_NONE;
    }

    /**
     * For a document, from the document level annotation, get the docType.
     *
     * @param document
     * @return List of List of Token String
     */
    public static HashMap<Integer, CoreMap> getDocLevelCategoryAnnotation(CoreMap document) {
        return document.get(CoreAnnotations.CategoryAnnotations.class);
    }

    /**
     * For a document, from the document level annotation, get the docType.
     *
     * @param document
     * @return List of List of Token String
     */
    public static void copyDocLevelCategoryAnnotationToParaLevel(CoreMap document, CoreMap paragraph) {
        paragraph.set(CoreAnnotations.CategoryAnnotations.class, document.get(CoreAnnotations.CategoryAnnotations.class));
    }

    /**
     * For a document, from the document level annotation, get the docType.
     *
     * @param document
     * @return List of List of Token String
     */
    public static void copyParaLevelCategoryAnnotationToDocLevel(CoreMap document, CoreMap paragraph) {
        document.set(CoreAnnotations.CategoryAnnotations.class, paragraph.get(CoreAnnotations.CategoryAnnotations.class));
        HashMap<Integer, CoreMap> categoryAnnotation = document.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation != null && categoryAnnotation.keySet().iterator().hasNext()) {
            int doctype = categoryAnnotation.keySet().iterator().next();
            CoreMap annotationCoreMap = categoryAnnotation.get(doctype);
            if (annotationCoreMap != null) {
                if (annotationCoreMap.size() != 0) {
                    annotationCoreMap.set(CoreAnnotations.TermTokensAnnotation.class, null);
                }
            }
        }
    }

    /**
     * Annotated a given Paragraph with given List of token and a given categoryId
     *
     * @param document
     * @param docType
     */
    public static void annotateDocTypeWithWeightAndUserObservation(Document document, int docType, float currentCategoryWeight) {
        HashMap<Integer, CoreMap> categoryAnnotation = new HashMap<>();
        CoreMap annotationCoreMap = new CoreMap();
        annotationCoreMap.set(CoreAnnotations.CurrentCategoryWeightFloat.class, currentCategoryWeight);
        annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, 0f);
        categoryAnnotation.put(docType, annotationCoreMap);
        document.set(CoreAnnotations.CategoryAnnotations.class, categoryAnnotation);
        document.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
    }

    public static void trainDocType(List<Classifier> classifiersForDocType, Document document) throws Exception {
        Document singleParaDoc = getEntireDocCollaspedAsSingleParagraph(document);
        //iterate over each paragraph
        if (singleParaDoc == null) {
            logger.error("Document can't be parsed. failed to train the model");
            return;
        }
        try {
            for (Classifier classifier : classifiersForDocType) {
                classifier.trainWithWeight(singleParaDoc);
                classifier.persistModel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (CoreMap paragraph : singleParaDoc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
                CategoryAnnotationHelper.copyCurrentCategoryWeightsToPriorForDocType(paragraph);
            }
        }
        copyParaLevelCategoryAnnotationToDocLevel(document, singleParaDoc.getParagraphs().get(0));
    }

    public static void classifyDocType(List<Classifier> classifiersForDocType, Document document) throws Exception {

        Document singleParaDoc = getEntireDocCollaspedAsSingleParagraph(document);
        try {
            for (Classifier classifier : classifiersForDocType) {
                classifier.classify(singleParaDoc.getId(), singleParaDoc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        copyParaLevelCategoryAnnotationToDocLevel(document, singleParaDoc.getParagraphs().get(0));
        //int docType = DocTypeAnnotationHelper.extractDocTypeFromSingleParaDocument(singleParaDoc.getParagraphs().get(0));
        //DocTypeAnnotationHelper.annotateDocType(document,docType);
    }

    public static Document getEntireDocCollaspedAsSingleParagraph(Document entireDocument) throws Exception {

        Document singleParaDocument = new Document();
        CoreMap singleParagraph = new CoreMap("collapsedPara", "collapsedPara");

        List<Token> allTokens = entireDocument.getParagraphs()
                .stream()
                .flatMap(paragraph -> paragraph.get(CoreAnnotations.TokenAnnotation.class).stream())
                .collect(Collectors.toList());
        singleParagraph.set(CoreAnnotations.TokenAnnotation.class, allTokens);
        DocTypeAnnotationHelper.copyDocLevelCategoryAnnotationToParaLevel(entireDocument, singleParagraph);
        List<CoreMap> paraList = new ArrayList<>();
        paraList.add(singleParagraph);

        boolean isUserObserved = entireDocument.containsKey(CoreAnnotations.IsUserObservationAnnotation.class);
        if (isUserObserved == true) {
            singleParagraph.set(CoreAnnotations.IsUserObservationAnnotation.class, isUserObserved);
        }
        singleParaDocument.set(CoreAnnotations.ParagraphsAnnotation.class, paraList);
        return singleParaDocument;
    }

    /**
     * Clear Prior training weight of all categories for a paragraph
     *
     * @param document
     */
    public static void clearPriorCategoryWeight(Document document) {
        HashMap<Integer, CoreMap> categoryAnnotation = document.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null) return;
        for (int categoryId : Category.getDocType()) {
            CoreMap annotationCoreMap = categoryAnnotation.get(categoryId);
            if (annotationCoreMap != null) {
                annotationCoreMap.set(CoreAnnotations.PriorCategoryWeightFloat.class, 0f);
                logger.debug("Cleared \t {} \t {}", document.getId(), categoryId);
            }
        }
    }
}
