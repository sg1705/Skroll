package com.skroll.benchmark;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.Classifier;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.factory.BenchmarkFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.util.ObjectPersistUtil;
import com.skroll.util.SkrollGuiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Created by saurabhagarwal on 6/13/15.
 */
public class ClassifierBenchmark {

    public static final Logger logger = LoggerFactory
            .getLogger(ClassifierBenchmark.class);

    DocumentFactory documentFactory;
    List<Classifier> classifiers;

    public static void main(String[] args) throws Exception, ObjectPersistUtil.ObjectPersistException {

        Injector injector = Guice.createInjector(new SkrollGuiceModule());
        DocumentFactory documentFactory = injector.getInstance(BenchmarkFSDocumentFactoryImpl.class);

        ClassifierBenchmark benchmark = new ClassifierBenchmark(documentFactory);
        QC qc = null;
        try {
            qc = benchmark.runQCOnBenchmarkFolder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("QC:" + qc.stats);

    }

    public ClassifierBenchmark(DocumentFactory documentFactory){
        this.documentFactory = documentFactory;
    }

    public QC qcDocument(Document firstDoc, Document secondDoc, QC qc){
        if (firstDoc==secondDoc){
            logger.error("both benchamrk docuemnts can not pointing to the same document");
            return null;
        }
        for(CoreMap firstDocParagraph : firstDoc.getParagraphs()) {
            for(CoreMap secondDocParagraph : secondDoc.getParagraphs()) {
                if (firstDocParagraph.getId().equalsIgnoreCase(secondDocParagraph.getId())) {
                    for (QC.Stats stats : qc.stats) {
                        if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(firstDocParagraph, stats.categoyId)) {
                            stats.overallOccurance++;
                        }
                        if(CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(secondDocParagraph, stats.categoyId)) {
                            stats.postClassificationOccurance++;
                        }
                        if (!CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(firstDocParagraph, stats.categoyId) &&
                                CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(secondDocParagraph, stats.categoyId))
                         {
                             // false positive
                             logger.trace("category [{}] type1Error [{}]",stats.categoyId, firstDocParagraph.getText());
                            stats.type1Error++;
                        } else if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(firstDocParagraph, stats.categoyId) &&
                                !CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(secondDocParagraph, stats.categoyId)) {
                            // false negative
                            logger.trace("category [{}] type2Error [{}]",stats.categoyId, firstDocParagraph.getText());
                            stats.type2Error++;
                        }
                    }
                    break;
                }
            }
        }
        return qc;
    }

    public QC runQCForBenchmark(String file, QC qc){
        Document firstDoc = null;
        Document secondDoc = null;
        try {
            firstDoc = documentFactory.get(file);
            secondDoc = documentFactory.get(file);
            if (firstDoc==secondDoc){
                logger.error("both benchamrk docuemnts can not pointing to the same document");
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        DocumentHelper.clearObservedParagraphs(secondDoc);
        final Document finalSecondDoc =secondDoc;
        try {
            classifiers.forEach(c -> c.classify(finalSecondDoc.getId(), finalSecondDoc));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qcDocument(firstDoc, secondDoc, qc);
    }

    public QC runQCOnBenchmarkFile (String file){
        QC qc = new QC();
        qc = runQCForBenchmark(file, qc);
        qc.calculateQCScore();
        return qc;

    }
    public QC runQCOnBenchmarkFolder() throws Exception {
        QC qc = new QC();
        List<String> docLists = documentFactory.getDocumentIds();
        for (String docName : docLists) {
                logger.info("Running Benchmark on the file: {}", docName);
                qc = runQCForBenchmark(docName, qc);
        }
        qc.calculateQCScore();
        return qc;
    }

}


