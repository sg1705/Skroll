package com.skroll.classifier;

import com.skroll.analyzer.model.DocumentAnnotatingModel;
import com.skroll.analyzer.model.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.TrainingDocumentAnnotatingModel;
import com.skroll.analyzer.model.bn.NBFCConfig;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * TOC Classifier
 * <p/>
 * Created by saurabh on 3/27/2015
 */
public class TOCExperimentClassifier extends ClassifierImpl {

    public static final Logger logger = LoggerFactory.getLogger(TOCExperimentClassifier.class);
    private TrainingDocumentAnnotatingModel trainingModel = null;
    private Map<String, ProbabilityDocumentAnnotatingModel> bniMap = new HashMap<>();

    private String modelName = "com.skroll.analyzer.model.TrainingDocumentAnnotatingModel.TOC";

    RandomVariableType wordType = RandomVariableType.WORD_IS_TOC_TERM;
    RandomVariableType paraType = RandomVariableType.PARAGRAPH_HAS_TOC;

    List<RandomVariableType> wordFeatures = Arrays.asList(
            RandomVariableType.WORD_IN_QUOTES,
            RandomVariableType.WORD_INDEX,
            RandomVariableType.WORD_IS_BOLD,
            RandomVariableType.WORD_IS_UNDERLINED,
            RandomVariableType.WORD_IS_ITALIC);

    List<RandomVariableType> paraFeatures = Arrays.asList(
            RandomVariableType.PARAGRAPH_NUMBER_TOKENS
            );

    List<RandomVariableType> paraDocFeatures = Arrays.asList(
            RandomVariableType.PARAGRAPH_NOT_IN_TABLE,
            RandomVariableType.PARAGRAPH_STARTS_WITH_BOLD,
            RandomVariableType.PARAGRAPH_STARTS_WITH_ITALIC,
            RandomVariableType.PARAGRAPH_STARTS_WITH_UNDERLINE,
            RandomVariableType.PARAGRAPH_ALL_WORDS_UPPERCASE,
            RandomVariableType.PARAGRAPH_IS_CENTER_ALIGNED,
            RandomVariableType.PARAGRAPH_HAS_ANCHOR
    );

    List<RandomVariableType> docFeatures = Arrays.asList(
            RandomVariableType.DOCUMENT_TOC_NOT_IN_TABLE,
            RandomVariableType.DOCUMENT_TOC_IS_BOLD,
            RandomVariableType.DOCUMENT_TOC_IS_ITALIC,
            RandomVariableType.DOCUMENT_TOC_IS_UNDERLINED,
            RandomVariableType. DOCUMENT_TOC_HAS_WORDS_UPPERCASE,
            RandomVariableType.DOCUMENT_TOC_IS_CENTER_ALIGNED,
            RandomVariableType.DOCUMENT_TOC_HAS_ANCHOR
    );
    List<RandomVariableType> wordVarList = Arrays.asList(
            RandomVariableType.WORD,
            RandomVariableType.FIRST_WORD
    );
    NBFCConfig nbfcConfig = new NBFCConfig(paraType, paraFeatures, paraDocFeatures, docFeatures, wordVarList);

    public TOCExperimentClassifier() {
        trainingModel = createModel();
    }
        public TrainingDocumentAnnotatingModel createModel() {
        //read the model
            TrainingDocumentAnnotatingModel localTrainingModel=null;
        try {
            localTrainingModel = (TrainingDocumentAnnotatingModel) objectPersistUtil.readObject(null, modelName);
        } catch (Throwable e) {
            logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one");
            localTrainingModel = null;
        }
        if (localTrainingModel == null) {
            localTrainingModel = new TrainingDocumentAnnotatingModel( wordType,
                     wordFeatures,
                     nbfcConfig);
        }
            return localTrainingModel;
        }

    @Override
    public void persistModel() throws ObjectPersistUtil.ObjectPersistException {
        objectPersistUtil.persistObject(null, trainingModel, modelName);
        logger.debug("persisted definedTermExtractionModel:" + trainingModel);
    }

    @Override
    public void train(Document doc) {
        trainingModel.updateWithDocument(doc);

    }
    @Override
    public void trainWithWeight(Document doc) {
        trainingModel.updateWithDocumentAndWeight(doc);

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
    public Object classify(String documentId, Document document) throws Exception {

        updateBNI(documentId, document, new ArrayList<CoreMap>());
//        ProbabilityDocumentAnnotatingModel bniModel = new ProbabilityDocumentAnnotatingModel(trainingModel.getTnbfModel(),
//                trainingModel.getHmm(), document, wordType, wordFeatures, paraType, paraFeatures, paraDocFeatures, docFeatures
//        );
//
//        logger.debug("TOC before annotate :");
//        for (CoreMap para: DocumentHelper.getTOCParagraphs(document)){
//            logger.debug(para.getId() +"\t" +DocumentHelper.getTOCLists(para).toString());
//            logger.debug("IsTOCAnnotation" +"\t" +para.get(CoreAnnotations.IsTOCAnnotation.class));
//        }
//        bniMap.put(documentId, bniModel);
//        bniModel.annotateDocument();
//
//        logger.debug("TOC after annotate :");
//
//        for (CoreMap para: DocumentHelper.getTOCParagraphs(document)){
//            logger.debug(para.getId() +"\t" +DocumentHelper.getTOCLists(para).toString());
//        }
        return document;
    }

    @Override
    public Object updateBNI(String documentId, Document document, List<CoreMap> observedParas) throws Exception {

        logger.debug("all observed paragraphs:");

        for (CoreMap para: DocumentHelper.getObservedParagraphs(document)){
            logger.debug(para.getText());
        }

        logger.debug("TOC before annotate :");
        for (CoreMap para: DocumentHelper.getTOCParagraphs(document)){
            logger.debug(para.getId() +"\t" +DocumentHelper.getTOCLists(para).toString());
            logger.debug("IsTOCAnnotation" +"\t" +para.get(CoreAnnotations.IsTOCAnnotation.class));
        }

        logger.debug("observedParas:" + "\t" + observedParas);



        for (CoreMap para: observedParas) {
            logger.debug("observedParas:IsTOCAnnotation" + "\t" + para.get(CoreAnnotations.IsTOCAnnotation.class));
        }
//        if (documentId == null || bniMap.get(documentId) == null) {
//            logger.error("Document Id is NULL or BNI is return null");
//            throw new Exception("Failed to updateBNI. check documentId : " + documentId);
//        }

        trainingModel = createModel();
        trainingModel.updateWithDocumentAndWeight(document);

        ProbabilityDocumentAnnotatingModel bniModel = new ProbabilityDocumentAnnotatingModel(trainingModel.getTnbfModel(),
                trainingModel.getHmm(), document, wordType, wordFeatures, nbfcConfig
        );

        // already done in the constructor
        //bniModel.updateBeliefWithObservation(observedParas);
        bniModel.annotateDocument();
        bniMap.put(documentId, bniModel);
        printBelieves(bniModel, document );
        logger.debug("TOC after annotate :");

        for (CoreMap para: DocumentHelper.getTOCParagraphs(document)){
            logger.debug(para.getId() +"\t" +DocumentHelper.getTOCLists(para).toString());
        }
        return document;
    }


    @Override
    public SortedMap<Category, Double> classifyDetailed(Document doc, int numOfTokens) {
        return null;
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
    void printBelieves(ProbabilityDocumentAnnotatingModel model, Document doc ){
        logger.trace("document level feature believes\n");

        double[][] dBelieves = model.getDocumentFeatureBelief();
        for (int i=0; i<dBelieves.length; i++){
            logger.trace(" " + model.DEFAULT_DOCUMENT_FEATURES);
            logger.trace(Arrays.toString(dBelieves[i]));
        }

        List<CoreMap> paraList = doc.getParagraphs();

        logger.trace("paragraph category believes\n");
        double[][] pBelieves = model.getParagraphCategoryBelief();

        for (int i=0; i<paraList.size(); i++){
            BNInference.normalizeLog(pBelieves[i]);
            logger.trace(paraList.get(i).getText());
            logger.trace(String.format("%d [%.0f %.0f]", i, pBelieves[i][0], pBelieves[i][1]));

        }
    }

    @Override
    public HashMap<String, HashMap<String, Double>> getBNIVisualMap(String documentId, int paraIndex) {
        return bniMap.get(documentId).toVisualMap(paraIndex);
    }


    @Override
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap(String documentId) {
        return trainingModel.toVisualMap();
    }

    @Override
    public List<Double> getProbabilityDataForDoc(String documentId) {
        return bniMap.get(documentId).toParaCategoryDump();
    }


}
