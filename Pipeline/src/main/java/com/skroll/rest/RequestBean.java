package com.skroll.rest;

import com.skroll.classifier.*;
import com.skroll.document.Document;
import com.skroll.document.factory.CorpusFSDocumentFactory;
import com.skroll.document.factory.DocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;

/**
 * Created by saurabh on 4/17/15.
 */
public class RequestBean {
    public static final Logger logger = LoggerFactory
            .getLogger(DocAPI.class);

    private String documentId;
    private Document document;
    private List<Classifier> classifiersForTraining;
    private List<Classifier> classifiersForClassify;

    private DocumentFactory documentFactory;

    public String getDocumentId() {
        return documentId;
    }

    public Document getDocument() {
        return document;
    }

    @Inject
    public RequestBean(@QueryParam("documentId") String documentId,
                       @Context HttpHeaders hh,
                       @CorpusFSDocumentFactory DocumentFactory documentFactory,
                       @ClassifierFactoryStrategyForTraining ClassifierFactoryStrategy classifierFactoryStrategyForTraining,
                       @ClassifierFactoryStrategyForClassify ClassifierFactoryStrategy classifierFactoryStrategyForClassify,
                       ClassifierFactory classifierFactory) throws Exception {
        if(documentId == null) {
            throw new Exception("documentId passed from viewer is null");
        }

        logger.info("Document Id: {}", documentId);

        if (documentId != null) {
            //fetch it from factory
            this.documentId = documentId;
            this.document = documentFactory.get(documentId);
        }

        this.classifiersForTraining = classifierFactory.getClassifiers(classifierFactoryStrategyForTraining,document);
        this.classifiersForClassify = classifierFactory.getClassifiers(classifierFactoryStrategyForClassify,document);
        this.documentFactory = documentFactory;
    }

    public List<Classifier> getClassifiersForTraining() {
        return classifiersForTraining;
    }

    public List<Classifier> getClassifiersForClassify() {
        return classifiersForClassify;
    }
    public DocumentFactory getDocumentFactory() { return documentFactory;}
}
