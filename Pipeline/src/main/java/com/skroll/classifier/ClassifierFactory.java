package com.skroll.classifier;

import com.skroll.analyzer.model.TrainingDocumentAnnotatingModel;
import com.skroll.document.Document;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabh on 4/16/15.
 */
public class ClassifierFactory {

    private static HashMap<Integer, Classifier> classifiers = new HashMap();
    private static HashMap<Integer, Category> categories = new HashMap();
    @Inject
    private ModelFactory modelFactory;

    static {
        categories.put(Category.DEFINITION,new DefinitionCategory());
        categories.put(Category.TOC,new TOCCategory());
    }

    public Classifier getClassifier(int categoryId) throws Exception {
        Category category = null;
        if(categories.containsKey(categoryId)){
            category = categories.get(categoryId);
        } else {
            throw new Exception("No category id found: "+ categoryId);
        }
        TrainingDocumentAnnotatingModel trainingDocumentAnnotatingModel = modelFactory.getTrainingModel(category);
        if (classifiers.containsKey(categoryId))
            return classifiers.get(categoryId);
        Classifier classifier =
                new ClassifierImpl(modelFactory, trainingDocumentAnnotatingModel, category);
        /*
        if (categoryId == 1) {
            classifier = new DefinitionExperimentClassifier(trainingDocumentAnnotatingModel);
        } else {
            classifier = new TOCExperimentClassifier();
        }
        */
        classifiers.put(categoryId, classifier);
        return classifier;
    }
   //Todo: need to add the classifier per document. currently returning all classifiers no matter what document is.
   public List<Classifier> getClassifier(Document document) throws Exception {
       List<Classifier> classifierList = new ArrayList<>();
       classifierList.add(getClassifier(Category.DEFINITION));
       classifierList.add(getClassifier(Category.TOC));

       return classifierList;
   }
}
