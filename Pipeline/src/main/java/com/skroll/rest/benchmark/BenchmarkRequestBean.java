package com.skroll.rest.benchmark;

import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.document.factory.BenchmarkFSDocumentFactory;
import com.skroll.document.factory.CorpusFSDocumentFactory;
import com.skroll.document.factory.DocumentFactory;

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
public class BenchmarkRequestBean {

    private String documentId;
    private List<Classifier> classifiers;
    private DocumentFactory benchmarkDocumentFactory;
    private DocumentFactory corpusDocumentFactory;

    public String getDocumentId() {
        return documentId;
    }


    @Inject
    public BenchmarkRequestBean(@QueryParam("documentId") String documentId,
                                @Context HttpHeaders hh,
                                @BenchmarkFSDocumentFactory DocumentFactory benchmarkDocumentFactory,
                                @CorpusFSDocumentFactory DocumentFactory corpusDocumentFactory,
                                ClassifierFactoryStrategy classifierFactoryStrategy,
                                ClassifierFactory classifierFactory) throws Exception {

        if(documentId == null) {
            MultivaluedMap<String, String> headerParams = hh.getRequestHeaders();
            Map<String, Cookie> pathParams = hh.getCookies();
             if(pathParams.get("documentId")!=null)
                 documentId = pathParams.get("documentId").getValue();
        }

        if (documentId != null) {
            //fetch it from factory
            this.documentId = documentId;
        }

        this.classifiers = classifierFactory.getClassifiers(classifierFactoryStrategy);
        this.benchmarkDocumentFactory = benchmarkDocumentFactory;
        this.corpusDocumentFactory = corpusDocumentFactory;
    }

    public List<Classifier> getClassifiers() {
        return classifiers;
    }
    public DocumentFactory getBenchmarkDocumentFactory() { return benchmarkDocumentFactory;}
    public DocumentFactory getCorpusDocumentFactory() { return corpusDocumentFactory;}
}
