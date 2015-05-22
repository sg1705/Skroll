package com.skroll.classifier;

import com.skroll.analyzer.model.RandomVariableType;
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
        categories.put(Category.TOC_1,new Category(Category.TOC_1,"com.skroll.classifier.TOC_1", RandomVariableType.WORD_IS_TOC_1_TERM,RandomVariableType.PARAGRAPH_HAS_TOC_1));
        categories.put(Category.TOC_2,new Category(Category.TOC_2,"com.skroll.classifier.TOC_2",RandomVariableType.WORD_IS_TOC_2_TERM,RandomVariableType.PARAGRAPH_HAS_TOC_2));
        categories.put(Category.TOC_3,new Category(Category.TOC_3,"com.skroll.classifier.TOC_3",RandomVariableType.WORD_IS_TOC_3_TERM,RandomVariableType.PARAGRAPH_HAS_TOC_3));
        categories.put(Category.TOC_4,new Category(Category.TOC_4,"com.skroll.classifier.TOC_4",RandomVariableType.WORD_IS_TOC_4_TERM,RandomVariableType.PARAGRAPH_HAS_TOC_4));
        categories.put(Category.TOC_5,new Category(Category.TOC_5,"com.skroll.classifier.TOC_5",RandomVariableType.WORD_IS_TOC_5_TERM,RandomVariableType.PARAGRAPH_HAS_TOC_5));
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
        classifiers.put(categoryId, classifier);
        return classifier;
    }
   //Todo: need to add the classifier per document. currently returning all classifiers no matter what document is.
   public List<Classifier> getClassifier(Document document) throws Exception {
       List<Classifier> classifierList = new ArrayList<>();
       classifierList.add(getClassifier(Category.DEFINITION));
       classifierList.add(getClassifier(Category.TOC_1));
       classifierList.add(getClassifier(Category.TOC_2));
       classifierList.add(getClassifier(Category.TOC_3));
       classifierList.add(getClassifier(Category.TOC_4));
       classifierList.add(getClassifier(Category.TOC_5));
       return classifierList;
   }
}
