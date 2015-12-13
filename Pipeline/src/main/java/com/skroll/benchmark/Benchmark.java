package com.skroll.benchmark;

import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.factory.DocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * Benchmark class is used for benchmarking the quality of output of our classifiers. It takes the files from benchmark folder and
 * run against the benchmark model and trained model and show the accuracy of trained model.
 * Created by saurabhagarwal on 6/13/15.
 */
public class Benchmark {

    public static final Logger logger = LoggerFactory
            .getLogger(Benchmark.class);

    DocumentFactory documentFactory;
    ClassifierFactory classifierFactory;
    ClassifierFactoryStrategy classifierFactoryStrategyForClassify;

    public Benchmark(DocumentFactory documentFactory, ClassifierFactory classifierFactory, ClassifierFactoryStrategy classifierFactoryStrategy){
        this.documentFactory = documentFactory;
        this.classifierFactory = classifierFactory;
        this.classifierFactoryStrategyForClassify = classifierFactoryStrategy;
    }

    public QC qcDocument(Document firstDoc, Document secondDoc, QC qc){
        if (firstDoc==secondDoc){
            logger.error("both benchamrk docuemnts can not pointing to the same document");
            return null;
        }
        QC localQC = new QC();
        for(CoreMap firstDocParagraph : firstDoc.getParagraphs()) {
            for(CoreMap secondDocParagraph : secondDoc.getParagraphs()) {
                if (firstDocParagraph.getId().equalsIgnoreCase(secondDocParagraph.getId())) {

                    for (QC.Stats stats : localQC.stats.values()) {
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
                             logger.info("Doc [{}] category [{}] type1Error [{}]", firstDoc.getId(), stats.categoyId, firstDocParagraph.getText());
                            stats.type1Error++;
                        } else if (CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(firstDocParagraph, stats.categoyId) &&
                                !CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(secondDocParagraph, stats.categoyId)) {
                            // false negative
                            logger.info("Doc [{}] category [{}] type2Error [{}]", firstDoc.getId(), stats.categoyId, firstDocParagraph.getText());
                            stats.type2Error++;
                        }
                    }

                    break;
                }
            }
        }
        localQC.calculateQCScore();
        logger.info("Stats for {} : {} ", firstDoc.getId(),localQC);
        qc.add(localQC);
        return qc;
    }

    public QC runQCForBenchmark(String file, QC qc) throws Exception {
        Document firstDoc = null;
        Document secondDoc = null;
        try {
            firstDoc = documentFactory.get(file);
            Thread.sleep(100);
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
        classifierFactory.getClassifiers(classifierFactoryStrategyForClassify, secondDoc).forEach(c -> c.classify(finalSecondDoc.getId(), finalSecondDoc));
        return qcDocument(firstDoc, secondDoc, qc);
    }

    public QC runQCOnBenchmarkFile (String file) throws Exception {
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


