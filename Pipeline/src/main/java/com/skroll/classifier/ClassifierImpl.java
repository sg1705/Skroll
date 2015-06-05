package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.analyzer.model.applicationModel.TrainingDocumentAnnotatingModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.parser.linker.DefinitionLinker;
import com.skroll.util.ObjectPersistUtil;
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
    protected TrainingDocumentAnnotatingModel trainingDocumentAnnotatingModel;
    @Override
    public ModelRVSetting getModelRVSetting() {
        return modelRVSetting;
    }

    protected ModelRVSetting modelRVSetting;

    public ClassifierImpl(ModelFactory modelFactory, TrainingDocumentAnnotatingModel trainingDocumentAnnotatingModel, ModelRVSetting modelRVSetting) {
        this.modelFactory = modelFactory;
        this.trainingDocumentAnnotatingModel = trainingDocumentAnnotatingModel;
        this.modelRVSetting = modelRVSetting;
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
    public Object updateBNI(String documentId,Document document, List<CoreMap> observedParas) throws Exception {
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
        trainingDocumentAnnotatingModel.updateWithDocument(doc);

    }
    @Override
    public void trainWithWeight( Document doc) {
        trainingDocumentAnnotatingModel.updateWithDocumentAndWeight(doc);

    }


    @Override
    public Object classify(String documentId, Document document) {
        try {
            return updateBNI(documentId, document, new ArrayList<CoreMap>());
        } catch (Exception e) {
            logger.error(String.format("Cannot classify documentId:%s for categoryId:%s",documentId, this.modelRVSetting.getCategoryId(), e));
        }
        return document;
    }

    @Override
    public Object classify(Document document, int numOfTokens) {
        return classify("documentId", document);
    }

    @Override
    public Object classify(String fileName, int numOfLines) {
        Document document = null;
        try {
            document = Parser.parseDocumentFromHtmlFile(fileName);
        } catch (ParserException e) {
            logger.error(String.format("Cannot parse file:%s",fileName), e);
        }
        return classify("documentId", document);
    }


    @Override
    public HashMap<String, HashMap<String, Double>> getBNIVisualMap( Document document, int paraIndex) {
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
    public void persistModel() throws ObjectPersistUtil.ObjectPersistException {
        modelFactory.saveTrainingModel(modelRVSetting);
    }
}