package com.skroll.rest;

import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.document.Document;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.document.factory.SingleParaFSDocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import java.util.List;

/** DocTypeRequestBean encapsulate all the information a DocType API functions need to act upon...
 * Created by saurabhagarwal on 8/30/2015.
 */
public class DocTypeRequestBean {
    public static final Logger logger = LoggerFactory
            .getLogger(DocAPI.class);

    private String documentId;
    private int docType;
    private Document document;
    private List<Classifier> classifiers;

    private DocumentFactory singleParaDocumentFactory;

    public String getDocumentId() {
        return documentId;
    }

    public Document getDocument() {
        return document;
    }

    public int getDocType() {
        return docType;
    }

    @Inject
    public DocTypeRequestBean(@QueryParam("documentId") String documentId,
                              @QueryParam("docType") int docType,
                              @Context HttpHeaders hh,
                              @SingleParaFSDocumentFactory DocumentFactory singleParaDocumentFactory,
                              ClassifierFactoryStrategy classifierFactoryStrategy,
                              ClassifierFactory classifierFactory) throws Exception {
        if(documentId == null) {
            throw new Exception("documentId passed from viewer is null!!");
        }
        logger.info("Document Id: {}",documentId);

        if (documentId != null) {
            //fetch it from factory
            this.documentId = documentId;
            this.document = singleParaDocumentFactory.get(documentId);
        }
        this.docType = docType;
        this.classifiers = classifierFactory.getClassifiers(classifierFactoryStrategy,document);
        this.singleParaDocumentFactory = singleParaDocumentFactory;
    }

    public List<Classifier> getClassifiers() {
        return classifiers;
    }
    public DocumentFactory getDocumentFactory() { return singleParaDocumentFactory;}
}
