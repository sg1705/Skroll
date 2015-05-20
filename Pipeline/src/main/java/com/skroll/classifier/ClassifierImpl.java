package com.skroll.classifier;

import com.skroll.analyzer.model.TrainingDocumentAnnotatingModel;
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
    public Category getCategory() {
        return category;
    }

    protected Category category;

    public ClassifierImpl(ModelFactory modelFactory, TrainingDocumentAnnotatingModel trainingDocumentAnnotatingModel, Category category) {
        this.modelFactory = modelFactory;
        this.trainingDocumentAnnotatingModel = trainingDocumentAnnotatingModel;
        this.category = category;
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
        modelFactory.getBNIModel(category, document);

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
    public void train(Category category, String fileName, int numOfLines) throws ParserException {
        Document document = Parser.parseDocumentFromHtmlFile(fileName);
        train( document);
    }

    @Override
    public Object classify(String documentId, Document document) throws Exception {
        return  updateBNI(documentId, document, new ArrayList<CoreMap>());

    }

    @Override
    public Object classify(Document document, int numOfTokens) throws Exception {
        return classify("documentId", document);
    }

    @Override
    public Object classify(String fileName, int numOfLines) throws Exception {
        Document document = Parser.parseDocumentFromHtmlFile(fileName);
        return classify("documentId", document);
    }


    @Override
    public HashMap<String, HashMap<String, Double>> getBNIVisualMap( Document document, int paraIndex) {
        return modelFactory.getBNIModel(category, document).toVisualMap(paraIndex);
    }


    @Override
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap() {
        return modelFactory.getTrainingModel(category).toVisualMap();
    }

    @Override
    public List<Double> getProbabilityDataForDoc(Document document) {
        return modelFactory.getBNIModel(category,document).toParaCategoryDump();
    }

    @Override
    public void persistModel() throws ObjectPersistUtil.ObjectPersistException {
        modelFactory.saveTrainingModel(category);
    }
}