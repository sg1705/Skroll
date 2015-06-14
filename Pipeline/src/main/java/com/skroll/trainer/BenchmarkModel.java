package com.skroll.trainer;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.*;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import com.skroll.util.SkrollGuiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by saurabhagarwal on 6/13/15.
 */
public class BenchmarkModel {

    public static final Logger logger = LoggerFactory
            .getLogger(BenchmarkModel.class);
    Injector injector = Guice.createInjector(new SkrollGuiceModule());
    ClassifierFactory classifierFactory = injector.getInstance(ClassifierFactory.class);
    private static String BENCHMARK;

    public static void main(String[] args) throws IOException, ObjectPersistUtil.ObjectPersistException {
        Configuration configuration = new Configuration();
        BenchmarkModel benchmark = new BenchmarkModel(configuration);
            QC qc = benchmark.runQCOnBenchmarkFolder();
            System.out.println("QC:" + qc.stats);
    }

    public BenchmarkModel(Configuration configuration) {
        //this.configuration = configuration;
        BENCHMARK = configuration.get("benchmarkFolder", "/tmp/");
        System.out.println("BENCHMARK Folder:" + BENCHMARK);
    }

    public Document fetchBenchmarkDocument(String documentId) {
        Document document;
        String jsonString;
        try {
            logger.debug("Fetching [{}] from filesystem [{}]", documentId,BENCHMARK);
            jsonString = Files.toString(new File(BENCHMARK + documentId), Charset.defaultCharset());
        } catch (IOException e) {
            logger.info("[{}] cannot be found", documentId);
            return null;
        }
        try {
            document = JsonDeserializer.fromJson(jsonString);
        } catch (Exception e) {
            logger.error("[{}] cannot be parsed", documentId);
            return null;
        }
        if (DocumentHelper.isLatestParser(document)) {
            //latest doc
            return document;
        }
        //doc is not the latest
        //now need to parse and return the latest
        try {
            document = Parser.reParse(document);
            //save it back since it is reparsed
        } catch (ParserException e) {
            logger.error("Cannot reparse document {}", document.getId());
        }

        return document;
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
                            stats.typeAError++;
                        } else if (CategoryAnnotationHelper.isCategoryId(firstDocParagraph, stats.categoyId) &&
                                !CategoryAnnotationHelper.isCategoryId(secondDocParagraph, stats.categoyId)) {
                            // false negative
                            stats.typeBError++;
                        }
                    }
                    break;
                }
            }
        }
        return qc;
    }

    public QC runQCForBenchmark(String file, QC qc){
        Document firstDoc = fetchBenchmarkDocument(file);
        Document secondDoc = fetchBenchmarkDocument(file);
        DocumentHelper.clearObservedParagraphs(secondDoc);
        try {
            classifierFactory.getClassifier(secondDoc).forEach(c -> c.classify(secondDoc.getId(),secondDoc));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return qcDocument(firstDoc, secondDoc, qc);
    }

    public QC runQCOnBenchmarkFile (String file){
        QC qc = new QC();
        return runQCForBenchmark(file, qc);

    }
    public QC runQCOnBenchmarkFolder()  {
        QC qc = new QC();
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(BENCHMARK));
        List<String> docLists = new ArrayList<String>();
        for (File f : iterable) {
            if (f.isFile()) {
                qc = runQCForBenchmark(f.getName(), qc);
            }
        }

        return qc;
    }

}


