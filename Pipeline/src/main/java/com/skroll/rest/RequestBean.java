package com.skroll.rest;

import com.skroll.classifier.*;
import com.skroll.document.Document;
import com.skroll.document.factory.CorpusFSDocumentFactory;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.document.factory.SingleParaFSDocumentFactory;
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
    private ClassifierFactory classifierFactory;
    private ClassifierFactoryStrategy classifierFactoryStrategyForTraining;
    private ClassifierFactoryStrategy  classifierFactoryStrategyForClassify;
    private ClassifierFactoryStrategy  classifierFactoryStrategyForDocType;

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
                       @ClassifierFactoryStrategyForDocType ClassifierFactoryStrategy classifierFactoryStrategyForDocType,
                       ClassifierFactory classifierFactory) throws Exception {
        this.documentFactory = documentFactory;
        this.classifierFactory = classifierFactory;
        this.classifierFactoryStrategyForTraining = classifierFactoryStrategyForTraining;
        this.classifierFactoryStrategyForClassify = classifierFactoryStrategyForClassify;
        this.classifierFactoryStrategyForDocType = classifierFactoryStrategyForDocType;

        if(documentId == null) {
            throw new Exception("documentId passed from viewer is null");
        }

        logger.info("Document Id: {}", documentId);

        if (documentId != null) {
            //fetch it from factory
            this.documentId = documentId;
            this.document = documentFactory.get(documentId);
        }

    }

    public List<Classifier> getClassifiersForTraining(Document document) throws Exception {
        return classifierFactory.getClassifiers(classifierFactoryStrategyForTraining,document);
    }

    public List<Classifier> getClassifiersForClassify(Document document) throws Exception {
        return classifierFactory.getClassifiers(classifierFactoryStrategyForClassify,document);

    }
    public List<Classifier> getClassifiersForDocType(Document document) throws Exception {
        return classifierFactory.getClassifiers(classifierFactoryStrategyForDocType,document);

    }
    public DocumentFactory getDocumentFactory() { return documentFactory;}
}
