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
import com.skroll.document.factory.DocumentFactory;
import com.skroll.util.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by saurabhagarwal on 1/19/15.
 */


/* current arguments for testing:
--trainWithOverride src/main/resources/trainingDocuments/indentures
--classify src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html
*/

public class Trainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(Trainer.class);

    public ClassifierFactory classifierFactory;
    public ClassifierFactoryStrategy classifierFactoryStrategy;
    public Configuration configuration;
    public DocumentFactory documentFactory;
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
    public void trainFolderUsingTrainingWeight (String preEvaluatedFolder) throws Exception {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(preEvaluatedFolder));
        List<String> docLists = new ArrayList<String>();
        int counter=0;
        for (File f : iterable) {
            if (f.isFile()) {
                displayHeapStats ();
                trainFileUsingTrainingWeight(f.getName());
            }
            counter++;
            if (counter ==10) {
                for ( Classifier classifier : classifierFactory.getClassifiers(classifierFactoryStrategy)) {
                    classifier.persistModel();
                }
                counter=0;
            }
        }
        for ( Classifier classifier : classifierFactory.getClassifiers(classifierFactoryStrategy)) {
            classifier.persistModel();
        }
    }

    public  void trainFileUsingTrainingWeight (String preEvaluatedFile) throws Exception {
        Document doc = documentFactory.get(preEvaluatedFile);
        //iterate over each paragraph
        if(doc== null){
            logger.error("Document can't be parsed. failed to train the model");
            return;
        }
        for(CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsUserObservationAnnotation.class)) {
                CategoryAnnotationHelper.clearPriorCategoryWeight(paragraph);
            }
        }
        try {
            for ( Classifier classifier : classifierFactory.getClassifiers(classifierFactoryStrategy)) {
                classifier.trainWithWeight(doc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
