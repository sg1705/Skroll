package com.skroll.trainer;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
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
    public Configuration configuration;
    public DocumentFactory documentFactory;
    public String PRE_EVALUATED_FOLDER;

    public void trainFolderUsingTrainingWeight (String preEvaluatedFolder) throws Exception {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(preEvaluatedFolder));
        List<String> docLists = new ArrayList<String>();
        for (File f : iterable) {
            if (f.isFile()) {
                trainFileUsingTrainingWeight(f.getName());
            }
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
            if (paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                TrainingWeightAnnotationHelper.clearOldTrainingWeight(paragraph);
            }
        }
        final Document finalDoc = doc;
        try {
            classifierFactory.getClassifiers(doc).forEach(c -> c.trainWithWeight(finalDoc));
            classifierFactory.getClassifiers(doc).forEach(c -> {
                try {
                    c.persistModel();
                } catch (Exception e) {
                    logger.error("Failed to persist classifier: %s"+ c.toString(), e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
