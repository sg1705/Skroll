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
public class ConvertDocToText {

    public static final Logger logger = LoggerFactory
            .getLogger(ConvertDocToText.class);

    public static DocumentFactory corpusDocumentFactory;
    static final String INPUT_FOLDER = "build/resources/main/preEvaluated/";
//    static final String OUTPUT_FOLDER = "build/resources/main/preEvaluatedTxt/";
    static final String OUTPUT_FILE_NAME = "build/resources/main/preEvaluatedTxtOneFile.txt";

    public static void convertDocsToText () throws Exception {

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

        try ( PrintWriter writer = new PrintWriter(OUTPUT_FILE_NAME, "UTF-8")) {
            FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(INPUT_FOLDER));
            int count =0;
            for (File f : iterable) {
                if (f.isFile()) {
                    Document doc = corpusDocumentFactory.get(f.getName());
                    //iterate over each paragraph
                    if (doc == null) {
                        logger.error("Document can't be parsed. failed to conver doc " + f.getName());
                        return;
                    }

                    count = 0;

                    //                try ( PrintWriter writer = new PrintWriter(OUTPUT_FOLDER + f.getName() + ".txt" , "UTF-8")) {
                    for (CoreMap paragraph : doc.getParagraphs()) {
                        String text = paragraph.getText();
                        String newText = text.replaceAll("\n|\t"," ");

                        if (text.length() > 50){
                            count ++;
//                            writer.println(newText);
                            writer.println(doc.getId()+"_"+count + "\t" +
                                    DocTypeAnnotationHelper.getDocType(doc) + "\t" + newText); // for mallet

                        }
                    }


                }
            }
        } catch (IOException ex){
                    ex.printStackTrace(System.out);
        }
    }

    public static void main(String[] argvs) throws Exception{
        convertDocsToText();
    }

}
