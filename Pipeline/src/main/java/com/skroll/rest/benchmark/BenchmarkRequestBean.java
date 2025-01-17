package com.skroll.rest.benchmark;

import com.skroll.benchmark.Benchmark;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.classifier.ClassifierFactoryStrategyForClassify;
import com.skroll.document.Document;
import com.skroll.document.factory.BenchmarkFSDocumentFactory;
import com.skroll.document.factory.CorpusFSDocumentFactory;
import com.skroll.document.factory.DocumentFactory;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;

/**
 * Created by saurabh on 4/17/15.
 */
public class BenchmarkRequestBean {

    private String documentId;
    private Document document;
    private DocumentFactory benchmarkDocumentFactory;
    private DocumentFactory corpusDocumentFactory;
    private Benchmark benchmark;
    public String getDocumentId() {
        return documentId;
    }


    @Inject
    public BenchmarkRequestBean(@QueryParam("documentId") String documentId,
                                @Context HttpHeaders hh,
                                @BenchmarkFSDocumentFactory DocumentFactory benchmarkDocumentFactory,
                                @CorpusFSDocumentFactory DocumentFactory corpusDocumentFactory,
                                @ClassifierFactoryStrategyForClassify ClassifierFactoryStrategy classifierFactoryStrategy,
                                ClassifierFactory classifierFactory) throws Exception {

        if(documentId == null) {
            throw new Exception("documentId passed from viewer is null");
        }

        if (documentId != null) {
            //fetch it from factory
            this.documentId = documentId;
            this.document = corpusDocumentFactory.get(documentId);
        }
        this.benchmarkDocumentFactory = benchmarkDocumentFactory;
        this.corpusDocumentFactory = corpusDocumentFactory;
        this.benchmark = new Benchmark(benchmarkDocumentFactory,classifierFactory,classifierFactoryStrategy);
    }

    public DocumentFactory getBenchmarkDocumentFactory() { return benchmarkDocumentFactory;}
    public DocumentFactory getCorpusDocumentFactory() { return corpusDocumentFactory;}
    public Benchmark getBenchmark() {return benchmark;}
}
