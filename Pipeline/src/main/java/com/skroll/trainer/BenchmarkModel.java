package com.skroll.trainer;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
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
public class BenchmarkModel {

    public static final Logger logger = LoggerFactory
            .getLogger(BenchmarkModel.class);

    DocumentFactory documentFactory;
    List<Classifier> classifiers;

    public static void main(String[] args) throws Exception, ObjectPersistUtil.ObjectPersistException {

       Injector injector = Guice.createInjector(new SkrollGuiceModule());
        ClassifierFactory classifierFactory = injector.getInstance(ClassifierFactory.class);
        DocumentFactory documentFactory = injector.getInstance(BenchmarkFSDocumentFactoryImpl.class);
        BenchmarkModel benchmark = new BenchmarkModel(documentFactory,classifierFactory.getClassifier());
        QC qc = null;
        try {
            qc = benchmark.runQCOnBenchmarkFolder();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("QC:" + qc.stats);

    }

    public BenchmarkModel(DocumentFactory documentFactory, List<Classifier> classifiers ){
        this.documentFactory = documentFactory;
        this.classifiers = classifiers;
    }

    public QC qcDocument(Document firstDoc, Document secondDoc, QC qc){
        for(CoreMap firstDocParagraph : firstDoc.getParagraphs()) {
            for(CoreMap secondDocParagraph : secondDoc.getParagraphs()) {
                if (firstDocParagraph.getId().equalsIgnoreCase(secondDocParagraph.getId())) {
                    for (QC.Stats stats : qc.stats) {
                        if (CategoryAnnotationHelper.isCategoryId(firstDocParagraph, stats.categoyId)){
                            stats.overallOccurance++;
                        } else {
                            stats.noOccurance++;
                        }
                        if (!CategoryAnnotationHelper.isCategoryId(firstDocParagraph, stats.categoyId) &&
                                CategoryAnnotationHelper.isCategoryId(secondDocParagraph, stats.categoyId))
                         {
                             // false positive
                             logger.debug("category [{}] type1Error [{}]",stats.categoyId, firstDocParagraph.getText());
                            stats.type1Error++;
                        } else if (CategoryAnnotationHelper.isCategoryId(firstDocParagraph, stats.categoyId) &&
                                !CategoryAnnotationHelper.isCategoryId(secondDocParagraph, stats.categoyId)) {
                            // false negative
                            logger.debug("category [{}] type2Error [{}]",stats.categoyId, firstDocParagraph.getText());
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
        List<String> docLists = documentFactory.getDocLists();
        for (String docName : docLists) {
                qc = runQCForBenchmark(docName, qc);
        }
        qc.calculateQCScore();
        return qc;
    }

}


