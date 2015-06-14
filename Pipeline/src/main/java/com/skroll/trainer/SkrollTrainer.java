package com.skroll.trainer;

import com.google.common.collect.FluentIterable;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentFactory;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.document.annotation.TrainingWeightAnnotationHelper;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import com.skroll.util.SkrollGuiceModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by saurabhagarwal on 1/19/15.
 */


/* current arguments for testing:
--trainWithOverride src/main/resources/trainingDocuments/indentures
--classify src/test/resources/analyzer/definedTermExtractionTesting/random-indenture.html
*/

public class SkrollTrainer {
    //The following line needs to be added to enable log4j
    public static final Logger logger = LoggerFactory
            .getLogger(SkrollTrainer.class);
    Injector injector = Guice.createInjector(new SkrollGuiceModule());
    ClassifierFactory classifierFactory = injector.getInstance(ClassifierFactory.class);
    Configuration configuration = new Configuration();
    DocumentFactory documentFactory = new DocumentFactory(configuration);
    String PRE_EVALUATED_FOLDER = configuration.get("preEvaluatedFolder", "/tmp/");

    public static void main(String[] args) throws IOException, ObjectPersistUtil.ObjectPersistException {

        SkrollTrainer skrollTrainer = new SkrollTrainer();

        //ToDO: use the apache common commandline
        if (args[0].equals("--trainWithWeight")) {
            logger.debug("folder Name :" + args[1]);
            skrollTrainer.trainFolderUsingTrainingWeight(args[1]);
        } else {
            skrollTrainer.trainFolderUsingTrainingWeight(skrollTrainer.PRE_EVALUATED_FOLDER);
        }
    }

    public void trainFolderUsingTrainingWeight (String preEvaluatedFolder)  {
        FluentIterable<File> iterable = Files.fileTreeTraverser().breadthFirstTraversal(new File(preEvaluatedFolder));
        List<String> docLists = new ArrayList<String>();
        for (File f : iterable) {
            if (f.isFile()) {
                trainFileUsingTrainingWeight(f.getName());
            }
        }

    }

    public  void trainFileUsingTrainingWeight (String preEvaluatedFile) {
        Document doc = documentFactory.get(preEvaluatedFile);
        //iterate over each paragraph
        for(CoreMap paragraph : doc.getParagraphs()) {
            if (paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)) {
                TrainingWeightAnnotationHelper.clearOldTrainingWeight(paragraph);
            }
        }
        final Document finalDoc = doc;
        try {
            classifierFactory.getClassifier(doc).forEach(c -> c.trainWithWeight(finalDoc));
            classifierFactory.getClassifier(doc).forEach(c -> {
                try {
                    c.persistModel();
                } catch (ObjectPersistUtil.ObjectPersistException e) {
                    logger.error("Failed to persist classifier: %s"+ c.toString(), e);
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
