package com.skroll.classifier;

import com.google.common.base.Objects;
import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
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

    protected ClassifierProto classifierProto;
    protected ModelFactory modelFactory;
    protected ModelRVSetting modelRVSetting;
    protected int id;

    public ClassifierImpl(int id, ClassifierProto classifierProto, ModelFactory modelFactory, ModelRVSetting modelRVSetting) {
        this.modelFactory = modelFactory;
        this.classifierProto = classifierProto;
        this.modelRVSetting = modelRVSetting;
        this.id = id;
    }

    @Override
    public ModelRVSetting getModelRVSetting() {
        return modelRVSetting;
    }

    @Override
    public int getId() {
        return id;
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

        logger.trace("Before annotate");
        CategoryAnnotationHelper.displayParagraphsAnnotatedWithAnyCategoryInDoc(document);
        modelFactory.createBNIModel(id, documentId, modelRVSetting, document);

        logger.trace("After annotate");
        CategoryAnnotationHelper.displayParagraphsAnnotatedWithAnyCategoryInDoc(document);
        DefinitionLinker linker = new DefinitionLinker();
        document = linker.linkDefinition(document);
        return document;
    }


    @Override
    public void train(Document doc) {
        modelFactory.getTrainingModel(id, modelRVSetting).updateWithDocument(doc);

    }

    @Override
    public void trainWithWeight(Document doc) {
        modelFactory.getTrainingModel(id, modelRVSetting).updateWithDocumentAndWeight(doc);
    }


    @Override
    public Object classify(String documentId, Document document) {
        try {
            return updateBNI(documentId, document, new ArrayList<CoreMap>());
        } catch (Exception e) {
            e.printStackTrace();

            logger.error(String.format("Cannot classify documentId:%s for categoryId:%s", documentId, this.classifierProto.getId(), e));

        }
        return document;
    }

    @Override
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getBNIVisualMap(String documentId, int paraIndex) {
        return modelFactory.getBNIModel(id, documentId).toVisualMap(paraIndex);
    }


    @Override
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap() {
        return modelFactory.getTrainingModel(id, modelRVSetting).toVisualMap();
    }

    @Override
    public List<Double> getProbabilityDataForDoc(String documentId) {
        return modelFactory.getBNIModel(id, documentId).toParaCategoryDump();
    }

    @Override
    public void persistModel() throws Exception {
        modelFactory.saveTrainingModel(id, modelRVSetting);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("Id", id)
                .add("classifierProto", classifierProto)
                .add("modelFactory", modelFactory)
                .add("modelRVSetting", modelRVSetting)
                .toString();
    }
}
