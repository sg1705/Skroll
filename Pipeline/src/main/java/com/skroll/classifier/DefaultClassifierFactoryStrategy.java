package com.skroll.classifier;

import com.skroll.document.Document;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.skroll.classifier.ClassifierId.*;

/**
 * Classifier Factory Strategy is responsible for returning the classifier Ids to classify the document.
 *  We can implement this interface to create the strategy for different document types.
 *  Created by saurabhagarwal on 8/6/15.
 */
public class DefaultClassifierFactoryStrategy implements ClassifierFactoryStrategy {
    /**
     * Default implementation of getCLassifierIds return list of classifier ids.
     * @return
     */
    public static final Logger logger = LoggerFactory.getLogger(DefaultClassifierFactoryStrategy.class);

    public List<ClassifierId> getClassifierIds(Document document)   {
        // By Default the document type is 10K
        int docType = Category.DOCTYPE_NONE;
        try {
            docType = DocTypeAnnotationHelper.getDocType(document);
        } catch (Exception e) {
            logger.warn("Document is not annotated with DocType Id, !!!! Assuming the docType is NONE in absence of DocType  !!!!");
        }
        List<ClassifierId> universal_classifier_ids = newArrayList(UNIVERSAL_DEF_CLASSIFIER, UNIVERSAL_TOC_CLASSIFIER);
        switch (docType) {
            case Category.TEN_K:
                return newArrayList(TEN_K_DEF_CLASSIFIER, TEN_K_TOC_CLASSIFIER, UNIVERSAL_DEF_CLASSIFIER, UNIVERSAL_TOC_CLASSIFIER);
            case Category.TEN_Q:
                return newArrayList(TEN_Q_DEF_CLASSIFIER, TEN_Q_TOC_CLASSIFIER,UNIVERSAL_DEF_CLASSIFIER, UNIVERSAL_TOC_CLASSIFIER);
            case Category.INDENTURE:
                return newArrayList(INDENTURE_DEF_CLASSIFIER, INDENTURE_TOC_CLASSIFIER,UNIVERSAL_DEF_CLASSIFIER, UNIVERSAL_TOC_CLASSIFIER);
            default:
                return universal_classifier_ids;
        }
    }

}
