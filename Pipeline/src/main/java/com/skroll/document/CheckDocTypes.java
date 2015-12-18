package com.skroll.document;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import com.skroll.document.factory.CorpusFSDocumentFactoryImpl;
import com.skroll.document.factory.DocumentFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by wei on 11/17/15.
 */
public class CheckDocTypes {

    public static final Logger logger = LoggerFactory
            .getLogger(CheckDocTypes.class);

    public static DocumentFactory corpusDocumentFactory;
    static final String INPUT_FOLDER = "build/resources/main/preEvaluated/";
//    static final String OUTPUT_FOLDER = "build/resources/main/preEvaluatedTxt/";
    static final String OUTPUT_FILE_NAME = "build/resources/main/preEvaluatedTxtOneFile.txt";

    public static void checkDocTypes() throws Exception {

        try {

            Injector injector = Guice.createInjector(new AbstractModule() {
                @Override
                protected void configure() {
                    bind(DocumentFactory.class)
                            .to(CorpusFSDocumentFactoryImpl.class);
                }
            });
            corpusDocumentFactory = injector.getInstance(DocumentFactory.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(INPUT_FOLDER));
        int count =0;
        int count10kAnn=0;
        int count10qAnn=0;
        int count10kActual = 0;
        int count10qActual =0;
        for (File f : iterable) {
            if (f.isFile()) {
                Document doc = corpusDocumentFactory.get(f.getName());
                //iterate over each paragraph
                if (doc == null) {
                    logger.error("Document can't be parsed. failed to check doc " + f.getName());
                    return;
                }

                count ++;
                int docType = DocTypeAnnotationHelper.getDocType(doc);
                if (doc.getParagraphs().size()==0) System.out.println("problem doc "+doc.getId());
                String actualType = doc.getParagraphs().get(0).getText().substring(0,4);
                if (docType == 101) count10kAnn++;
                if (docType == 102) count10qAnn++;
                if (actualType.equals("10-K")) count10kActual++;
                if (actualType.equals("10-Q")) count10qActual++;
                System.out.println(count + ", " + doc.getId() + ", " + docType + ", " + actualType);

            }
        }
        System.out.println("   actual count: 10-K, 10-Q" + count10kActual + "\t"  + count10qActual);
        System.out.println("annotated count: 10-K, 10-Q" + count10kAnn+ "\t"  + count10qAnn);
    }

    public static void main(String[] argvs) throws Exception{
        checkDocTypes();
    }

}
