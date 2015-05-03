package com.skroll.rest;

import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.DocumentFactory;
import com.skroll.document.Document;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saurabh on 4/17/15.
 */
public class RequestBean {

    private String documentId;
    private Document document;
    private List<Classifier> classifiers;
    private  DocumentFactory documentFactory;

    public String getDocumentId() {
        return documentId;
    }



    public Document getDocument() {
        return document;
    }


    @Inject
    public RequestBean(@QueryParam("documentId") String documentId,
                       @Context HttpHeaders hh,
                       DocumentFactory documentFactory,
                       ClassifierFactory classifierFactory) throws Exception {



        if(documentId == null) {
            MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
            Map<String, Cookie> pathParams = hh.getCookies();
             if(pathParams.get("documentId")!=null)
                 documentId = pathParams.get("documentId").getValue();
        }

        System.out.println("DocumentId:"+documentId);

        if (documentId != null) {
            //fetch it from factory
            this.documentId = documentId;
            this.document = documentFactory.get(documentId);
            System.out.println(this.document);
        }

        this.classifiers = classifierFactory.getClassifier(this.document);
        this.documentFactory = documentFactory;
    }

    public List<Classifier> getClassifiers() {
        return classifiers;
    }
    public DocumentFactory getDocumentFactory() { return documentFactory;}
}
