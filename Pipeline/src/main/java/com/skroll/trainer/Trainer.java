package com.skroll.trainer;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.skroll.classifier.Classifier;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierFactoryStrategy;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.DocTypeAnnotationHelper;
import com.skroll.document.factory.DocumentFactory;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;


/**
 * Commandline utility to train the model, annotate docType and classify Doc Type:
 * Valid commands are annotateDocType, trainDocTypeModel, trainWithWeight and classifyDocType"
 * Created by saurabhagarwal on 1/19/15.
 */


public class Trainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(Trainer.class);

    public ClassifierFactory classifierFactory;
    public ClassifierFactoryStrategy classifierFactoryStrategy;
    public Configuration configuration;
    public DocumentFactory corpusDocumentFactory;
    public DocumentFactory singleParaDocumentFactory;

    public String PRE_EVALUATED_FOLDER;

    public void displayHeapStats (){
        int mb = 1024*1024;

        //Getting the runtime reference from system
        Runtime runtime = Runtime.getRuntime();

        logger.debug("##### Heap utilization statistics [MB] #####");

        //Print used memory
        logger.debug("Used Memory:{} MB",
                (runtime.totalMemory() - runtime.freeMemory()) / mb);

        //Print free memory
        logger.debug("Free Memory:{} MB", runtime.freeMemory() / mb);

        //Print total available memory
        logger.debug("Total Memory: {} MB", runtime.totalMemory() / mb);

        //Print Maximum available memory
        logger.debug("Max Memory:{} MB", runtime.maxMemory() / mb);
    }

    public static void main(String[] args) throws ObjectPersistUtil.ObjectPersistException, Exception {

        Options options = new Options();
        options.addOption("c", "command", true, "Command to execute");
        options.addOption("f", "folder", true, "provide the location of folder  to train or classify the documents");
        options.addOption("t", "doctype", true, "Use in annotate Doc Type in the document");
        options.addOption("w", "weight", true, "Use to set weight for DocType Category at document level");

        CommandLineParser parser=new GnuParser();
        CommandLine cmd=parser.parse(options,args);

        if (cmd.getOptions().length < 1) {
            printHelpMessage();
            throw new ParseException("Missing commandline arguments. ");
        }

        switch (cmd.getOptionValue("command").trim()) {
            case "help":
                System.out.println(
                                "############################################# \n" +
                                "For following four commands below, the default folder is  \"build/resources/main/preEvaluated/\"."+
                                "you don't have to provide the -f option, if you are using the default folder. \n" +
                                "#To annotate Doc type of files of a folder specified by -f, from command line \n" +
                                "./gradlew trainer -Dexec.args=\"-c annotateDocType -f build/resources/main/preEvaluated/ -t 101 -w 1f\" \n" +
                                "#To train Doc type Model from a folder containing training files \n" +
                                "./gradlew trainer -Dexec.args=\"-c trainDocTypeModel -f build/resources/main/preEvaluated/\"\n" +
                                "#How to train from command line \n" +
                                "./gradlew trainer -Dexec.args=\"-c trainWithWeight -f build/resources/main/preEvaluated/\"\n" +
                                "#How to classify doctype from command line \n" +
                                "./gradlew trainer -Dexec.args=\"-c classifyDocType -f build/resources/main/preEvaluated/\"\n\"" +
                                "#############################################");
                return;
        }

        String folder = cmd.getOptionValue("folder");
        if(folder == null){
            folder = "build/resources/main/preEvaluated/";
        }

        logger.info("Folder name {}", cmd.getOptionValue("folder"));
        DocTypeTrainerAndClassifier docTypeTrainerAndClassifier = new DocTypeTrainerAndClassifier(folder);
        CategoryTrainer categoryTrainer = new CategoryTrainer(folder);

        switch (cmd.getOptionValue("command").trim()) {
            case "annotateDocType":
                categoryTrainer.annotateDocType(Integer.parseInt(cmd.getOptionValue("doctype")), Float.parseFloat(cmd.getOptionValue("weight")));//Category.INDENTURE, 1f);
                break;
            case "trainDocTypeModel":
                docTypeTrainerAndClassifier.trainDocTypeModelWithWeight();
                break;
            case "trainWithWeight":
                categoryTrainer.trainFolderUsingTrainingWeight();
                break;
            case "classifyDocType":
                docTypeTrainerAndClassifier.classifyAndStoreDocType();
                break;
            case "findTrainingDoc":
                categoryTrainer.findTrainingDoc();
                break;
            default:
                throw new IllegalArgumentException("Invalid command:" + cmd.getOptionValue("command") + " Valid commands are annotateDocType, trainDocTypeModel, trainWithWeight and classifyDocType");
        }
    }

    public static void printHelpMessage() {
        System.out.println(
                "############################################# \n" +
                        "For following four commands below, the default folder is  \"data/preEvaluated/\"." +
                        "you don't have to provide the -f option, if you are using the default folder. \n" +
                        "#To annotate Doc type of files of a folder specified by -f, from command line \n" +
                        "./trainer.sh -c annotateDocType -f data/preEvaluated/ -t 101 -w 1f \n" +
                        "#To train Doc type Model from a folder containing training files \n" +
                        "./trainer.sh -c trainDocTypeModel -f data/preEvaluated/ \n" +
                        "#To train from command line \n" +
                        "./trainer.sh -c trainWithWeight -f data/preEvaluated/ \n" +
                        "#To classify doctype from command line \n" +
                        "./trainer.sh -c classifyDocType -f data/preEvaluated/ \n\"" +
                        "#To find training documents from command line \n" +
                        "./trainer.sh -c findTrainingDoc -f data/preEvaluated/ \n\"" +
                        "#############################################");
    }
    public void trainFolderUsingTrainingWeight () throws Exception {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(PRE_EVALUATED_FOLDER));
        for (File f : iterable) {
            if (f.isFile()) {
                displayHeapStats();
                Document doc = corpusDocumentFactory.get(f.getName());
                //iterate over each paragraph
                if (doc == null) {
                    logger.error("Document can't be parsed. failed to train the model");
                    return;
                }
                for (CoreMap paragraph : doc.getParagraphs()) {
                    if (paragraph.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
                        CategoryAnnotationHelper.clearPriorCategoryWeight(paragraph);
                    }
                }
                try {
                    for (Classifier classifier : classifierFactory.getClassifiers(classifierFactoryStrategy, doc)) {
                        classifier.trainWithWeight(doc);
                        classifier.persistModel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void trainDocTypeModelWithWeight () throws Exception {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(PRE_EVALUATED_FOLDER));
        for (File f : iterable) {
            if (f.isFile()) {
                displayHeapStats();
                Document doc = singleParaDocumentFactory.get(f.getName());
                //iterate over each paragraph
                if (doc == null) {
                    logger.error("Document can't be parsed. failed to train the model");
                    return;
                }
                for (CoreMap paragraph : doc.getParagraphs()) {
                    if (paragraph.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
                        CategoryAnnotationHelper.clearPriorCategoryWeight(paragraph);
                    }
                }
                try {
                    for (Classifier classifier : classifierFactory.getClassifiers(classifierFactoryStrategy, doc)) {
                        classifier.trainWithWeight(doc);
                        classifier.persistModel();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public  void annotateDocType(int docType, float weight) throws Exception {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(PRE_EVALUATED_FOLDER));
        int counter = 0;
        for (File f : iterable) {
            if (f.isFile()) {
                Document doc = corpusDocumentFactory.get(f.getName());
                DocTypeAnnotationHelper.annotateDocTypeWithWeightAndUserObservation(doc, docType, weight);
                logger.debug("annotateDocType: {}", f.getName());
                corpusDocumentFactory.saveDocument(doc);
            }
        }
    }

    public  void classifyAndStoreDocType() throws Exception {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(PRE_EVALUATED_FOLDER));
        for (File f : iterable) {
            if (f.isFile()) {
                Document document = corpusDocumentFactory.get(f.getName());
                DocTypeAnnotationHelper.classifyDocType(classifierFactory.getClassifiers(classifierFactoryStrategy, document),document);
                logger.info("classify file {} as docType {}", f.getName(), DocTypeAnnotationHelper.getDocType(document));
                corpusDocumentFactory.saveDocument(document);
            }
        }
    }

    public void findTrainingDoc () {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(PRE_EVALUATED_FOLDER));
        int counter = 0;
        for (File f : iterable) {
            if (f.isFile()) {
                Document doc = null;
                try {
                    doc = corpusDocumentFactory.get(f.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error(" {} file can't be found.",f.getName());
                }
                //iterate over each paragraph
                if (doc == null) {
                    logger.error(" {} document can't be parsed.",doc.getId());
                    continue;
                }
                boolean isUserObservationAnnotation = false;
                for (CoreMap paragraph : doc.getParagraphs()) {
                    if (!paragraph.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
                        isUserObservationAnnotation = true;
                        break;
                    }
                }
                if (isUserObservationAnnotation) {
                    System.out.println(doc.getId());
                    counter++;
                }
            }
        }
        logger.info("Number of trained files: {}", counter);
    }
}
