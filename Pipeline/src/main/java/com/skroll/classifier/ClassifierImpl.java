package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TOCModelRVSetting;
import com.skroll.classifier.factory.ModelFactory;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.linker.DefinitionLinker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by saurabhagarwal on 1/18/15.
 */
public class ClassifierImpl implements Classifier {
    // Initialize the list of category this classifier support.

    public static final Logger logger = LoggerFactory.getLogger(ClassifierImpl.class);

    protected ModelFactory modelFactory;
    private int classifierId;
    private List<Integer> categoryIds = null;
    protected ModelRVSetting modelRVSetting;

    //protected TrainingDocumentAnnotatingModel trainingDocumentAnnotatingModel;
    @Override
    public ModelRVSetting getModelRVSetting() {
        return modelRVSetting;
    }


    public ClassifierImpl(int classifierId, String classifierName, List<Integer> categoryIds, ModelFactory modelFactory) {
        this.modelFactory = modelFactory;
        this.classifierId = classifierId;
        this.categoryIds = categoryIds;
        this.modelRVSetting = new TOCModelRVSetting(classifierId, classifierName, categoryIds.size());
    }

    public List<String> extractTokenFromDoc(Document doc) {
        List<CoreMap> paragraphs = doc.getParagraphs();
        Set<String> words = new HashSet<String>();
        for (CoreMap paragraph : paragraphs) {
            List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);
            for (Token token : tokens) {
                words.add(token.getText());
            }
        }
        return new ArrayList<String>(words);
    }


    @Override
    public Object updateBNI(String documentId, Document document, List<CoreMap> observedParas) throws Exception {
        if (!observedParas.isEmpty())
            logger.debug("observedParas:" + "\t" + observedParas);

        logger.debug("Before annotate");
        CategoryAnnotationHelper.displayCategoryOfDoc(document);
        modelFactory.createBNIModel(modelRVSetting, document);

        logger.debug("After annotate");
        CategoryAnnotationHelper.displayCategoryOfDoc(document);
        DefinitionLinker linker = new DefinitionLinker();
        document = linker.linkDefinition(document);
        return document;
    }


    @Override
    public void train(Document doc) {
        modelFactory.getTrainingModel(modelRVSetting).updateWithDocument(doc);

    }

    @Override
    public void trainWithWeight(Document doc) {
        modelFactory.getTrainingModel(modelRVSetting).updateWithDocumentAndWeight(doc);
    }


    @Override
    public Object classify(String documentId, Document document) {
        try {
            return updateBNI(documentId, document, new ArrayList<CoreMap>());
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(String.format("Cannot classify documentId:%s for categoryId:%s", documentId, this.modelRVSetting.getClassifierId(), e));
        }
        return document;
    }

    @Override
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getBNIVisualMap(Document document, int paraIndex) {
        return modelFactory.getBNIModel(modelRVSetting).toVisualMap(paraIndex);
    }


    @Override
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap() {
        return modelFactory.getTrainingModel(modelRVSetting).toVisualMap();
    }

    @Override
    public List<Double> getProbabilityDataForDoc(Document document) {
        return modelFactory.getBNIModel(modelRVSetting).toParaCategoryDump();
    }

    @Override
    public void persistModel() throws Exception {
        modelFactory.saveTrainingModel(modelRVSetting);
    }

    public int getClassifierId() {
        return classifierId;
    }

    public List<Integer> getCategoryIds() {
        return categoryIds;
    }
}
