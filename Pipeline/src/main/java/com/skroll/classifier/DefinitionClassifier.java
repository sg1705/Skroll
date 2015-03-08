package com.skroll.classifier;

import com.google.gson.reflect.TypeToken;
import com.skroll.analyzer.model.DefinedTermExtractionModel;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.parser.linker.DefinitionLinker;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.SortedMap;

/**
 * Created by saurabhagarwal on 1/18/15.
 */
public class DefinitionClassifier extends ClassifierImpl{

    public static final Logger logger = LoggerFactory
            .getLogger(DefinitionClassifier.class);

    private DefinedTermExtractionModel definedTermExtractionModel = null;
    private  Type dtemType = null;
    private  String dtemModelName = "com.skroll.analyzer.model.DefinedTermExtractionModel.DefinitionDTEM";

    Configuration configuration = new Configuration();
    private String modelFolderName = configuration.get("modelFolder","/tmp");
    protected ObjectPersistUtil objectPersistUtil = new ObjectPersistUtil(modelFolderName);

    public DefinitionClassifier() {
        try {
            dtemType = new TypeToken<DefinedTermExtractionModel>() {}.getType();
            definedTermExtractionModel = (DefinedTermExtractionModel) objectPersistUtil.readObject(dtemType,dtemModelName);
            logger.debug("definedTermExtractionModel:" + definedTermExtractionModel);

        } catch (Throwable e) {
            logger.warn("definedTermExtractionModel is not found. creating new one" );
            definedTermExtractionModel=null;
        }
        if (definedTermExtractionModel==null) {

            definedTermExtractionModel = new DefinedTermExtractionModel();
        }


    }
    @Override
    public void persistModel() throws ObjectPersistUtil.ObjectPersistException {
        objectPersistUtil.persistObject(dtemType,definedTermExtractionModel,dtemModelName);
        logger.debug("persisted definedTermExtractionModel:" + definedTermExtractionModel);
    }

    @Override
    public void train(Document doc) {
        definedTermExtractionModel.updateWithDocument(doc);
        definedTermExtractionModel.compile();

    }

    @Override
    public void train(Category category, Document doc) {
        train(doc);
    }

    @Override
    public void train(Category category, String fileName, int numOfLines) throws ParserException {
        Document document = Parser.parseDocumentFromHtmlFile(fileName);
        train(document);
    }


    @Override
    public Object classify(Document document) throws Exception {

        logger.debug("definitions before annotate");

        for (CoreMap para: DocumentHelper.getDefinitionParagraphs(document)){
            logger.debug(para.getText());
            logger.debug(DocumentHelper.getDefinedTermTokensInParagraph(para).toString());
        }

        definedTermExtractionModel.annotateDefinedTermsInDocument(document);

        logger.debug("definitions after annotate");
        for (CoreMap para:DocumentHelper.getDefinitionParagraphs(document)){
            logger.debug(para.getText());
            logger.debug(DocumentHelper.getDefinedTermTokensInParagraph(para).toString());
        }
        logger.debug("Document Size:" + DocumentHelper.getDefinitionParagraphs(document).size());

        DefinitionLinker linker = new DefinitionLinker();
        document = linker.linkDefinition(document);
        return document;
    }

    @Override
    public SortedMap<Category, Double> classifyDetailed(Document doc, int numOfTokens) {
        return null;
    }

    @Override
    public Object classify(Document document, int numOfTokens) throws Exception {
        return classify(document);
    }
    @Override
    public Object classify(String fileName, int numOfLines) throws Exception {
        Document document = Parser.parseDocumentFromHtmlFile(fileName);
        return classify(document);
    }


}
