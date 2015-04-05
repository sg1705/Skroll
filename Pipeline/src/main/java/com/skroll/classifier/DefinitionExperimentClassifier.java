package com.skroll.classifier;

import com.google.gson.reflect.TypeToken;
import com.skroll.analyzer.model.ProbabilityDocumentAnnotatingModel;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.TrainingDocumentAnnotatingModel;
import com.skroll.analyzer.model.bn.inference.BNInference;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.DocumentHelper;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.parser.linker.DefinitionLinker;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.*;

/**
 * Created by saurabhagarwal on 1/18/15.
 */
public class DefinitionExperimentClassifier extends ClassifierImpl{

    public static final Logger logger = LoggerFactory
            .getLogger(DefinitionExperimentClassifier.class);

    private TrainingDocumentAnnotatingModel trainingModel= null;

    private Map<String, ProbabilityDocumentAnnotatingModel> bniMap = new HashMap<>();
    private  Type dtemType = null;

    public String getDtemModelName() {
        return dtemModelName;
    }

    private  String dtemModelName = "com.skroll.analyzer.model.TrainingDocumentAnnotatingModel.DefinitionDTEM";



    public DefinitionExperimentClassifier() {
        trainingModel = createModel();
    }
        public TrainingDocumentAnnotatingModel createModel() {
            TrainingDocumentAnnotatingModel localTrainingModel=null;
        try {
            dtemType = new TypeToken<TrainingDocumentAnnotatingModel>() {}.getType();
            localTrainingModel = (TrainingDocumentAnnotatingModel) objectPersistUtil.readObject(dtemType,dtemModelName);
            logger.trace("TrainingDocumentAnnotatingModel:" + localTrainingModel);

        } catch (Throwable e) {
            logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one" );
            localTrainingModel=null;
        }
        if (localTrainingModel==null) {

            localTrainingModel = new TrainingDocumentAnnotatingModel();
        }


            return localTrainingModel;
        }
    @Override
    public void persistModel() throws ObjectPersistUtil.ObjectPersistException {
        objectPersistUtil.persistObject(dtemType,trainingModel,dtemModelName);
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
        return  updateBNI(documentId, document, new ArrayList<CoreMap>());
       /*
        RandomVariableType wordType = RandomVariableType.WORD_IS_DEFINED_TERM;
        RandomVariableType paraType = RandomVariableType.PARAGRAPH_HAS_DEFINITION;
        List<RandomVariableType> wordFeatures = ProbabilityDocumentAnnotatingModel.DEFAULT_WORD_FEATURES;
        List<RandomVariableType> paraFeatures = ProbabilityDocumentAnnotatingModel.DEFAULT_PARAGRAPH_FEATURES;
        List<RandomVariableType> paraDocFeatures = ProbabilityDocumentAnnotatingModel.DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL;
        List<RandomVariableType> docFeatures = ProbabilityDocumentAnnotatingModel.DEFAULT_DOCUMENT_FEATURES;

        ProbabilityDocumentAnnotatingModel bniModel =  new ProbabilityDocumentAnnotatingModel( trainingModel.getTnbfModel(),
                trainingModel.getHmm(), document, wordType, wordFeatures, paraType, paraFeatures, paraDocFeatures, docFeatures
        );
        logger.debug("definitions before annotate");

        for (CoreMap para: DocumentHelper.getDefinitionParagraphs(document)){
            logger.debug(para.getId() +"\t" + DocumentHelper.getDefinedTermTokensInParagraph(para).toString());
        }

        bniMap.put(documentId, bniModel);
        bniModel.annotateDocument();

        logger.debug("definitions after annotate");
        for (CoreMap para:DocumentHelper.getDefinitionParagraphs(document)){
            logger.debug(para.getId() +"\t" + DocumentHelper.getDefinedTermTokensInParagraph(para).toString());
        }
        logger.debug("Document Size:" + DocumentHelper.getDefinitionParagraphs(document).size());

        DefinitionLinker linker = new DefinitionLinker();
        document = linker.linkDefinition(document);
        return document;
        */
    }

    @Override
    public Object updateBNI(String documentId,Document document, List<CoreMap> observedParas) throws Exception {


        logger.debug("definitions before annotate");

        for (CoreMap para: DocumentHelper.getDefinitionParagraphs(document)){
            logger.debug(para.getId() +"\t" +DocumentHelper.getDefinedTermTokensInParagraph(para).toString());
        }
        logger.debug("Number of Defined paras:" + DocumentHelper.getDefinitionParagraphs(document).size());
        logger.debug("observedParas:" + "\t" + observedParas);
        for (CoreMap para: observedParas) {
            logger.debug("observedParas:IsDefinitionAnnotation" + "\t" + para.get(CoreAnnotations.IsDefinitionAnnotation.class));
        }
       /*
        if(documentId==null || bniMap.get(documentId)==null){
            throw new Exception("Failed to updateBNI. check documentId : " +documentId);
        }
        */
        trainingModel = createModel();
        trainingModel.updateWithDocumentAndWeight(document);

        RandomVariableType wordType = RandomVariableType.WORD_IS_DEFINED_TERM;
        RandomVariableType paraType = RandomVariableType.PARAGRAPH_HAS_DEFINITION;
        List<RandomVariableType> wordFeatures = ProbabilityDocumentAnnotatingModel.DEFAULT_WORD_FEATURES;
        List<RandomVariableType> paraFeatures = ProbabilityDocumentAnnotatingModel.DEFAULT_PARAGRAPH_FEATURES;
        List<RandomVariableType> paraDocFeatures = ProbabilityDocumentAnnotatingModel.DEFAULT_PARAGRAPH_FEATURES_EXIST_AT_DOC_LEVEL;
        List<RandomVariableType> docFeatures = ProbabilityDocumentAnnotatingModel.DEFAULT_DOCUMENT_FEATURES;

        ProbabilityDocumentAnnotatingModel bniModel =  new ProbabilityDocumentAnnotatingModel( trainingModel.getTnbfModel(),
                trainingModel.getHmm(), document, wordType, wordFeatures, paraType, paraFeatures, paraDocFeatures, docFeatures
        );

        bniModel.updateBeliefWithObservation(observedParas);
        bniModel.annotateDocument();
        bniMap.put(documentId, bniModel);
        printBelieves(bniModel, document);

        logger.debug("definitions after annotate");
        for (CoreMap para:DocumentHelper.getDefinitionParagraphs(document)){
            logger.debug(para.getId() +"\t" + DocumentHelper.getDefinedTermTokensInParagraph(para).toString());
        }
        logger.debug("Number of Defined paras:" + DocumentHelper.getDefinitionParagraphs(document).size());

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
        return classify("documentId", document);
    }
    @Override
    public Object classify(String fileName, int numOfLines) throws Exception {
        Document document = Parser.parseDocumentFromHtmlFile(fileName);
        return classify("documentId",document);
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
    public List<Double> getProbabilityDataForDoc(String documentId) {
        return bniMap.get(documentId).toParaCategoryDump();
    }



    @Override
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap(String documentId) {
        return trainingModel.toVisualMap();
    }

}