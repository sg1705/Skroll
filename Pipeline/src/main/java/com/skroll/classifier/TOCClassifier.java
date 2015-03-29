package com.skroll.classifier;

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
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * TOC Classifier
 * <p/>
 * Created by saurabh on 3/27/2015
 */
public class TOCClassifier extends ClassifierImpl {

    public static final Logger logger = LoggerFactory.getLogger(TOCClassifier.class);
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
            RandomVariableType.PARAGRAPH_NUMBER_TOKENS);

    List<RandomVariableType> paraDocFeatures = Arrays.asList(
            RandomVariableType.PARAGRAPH_ALL_WORDS_UPPERCASE,
            RandomVariableType.PARAGRAPH_IS_CENTER_ALIGNED,
            RandomVariableType.PARAGRAPH_HAS_ANCHOR);

    List<RandomVariableType> docFeatures = Arrays.asList(
            RandomVariableType. DOCUMENT_TOC_HAS_WORDS_UPPERCASE,
            RandomVariableType.DOCUMENT_TOC_IS_CENTER_ALIGNED,
            RandomVariableType.DOCUMENT_TOC_HAS_ANCHOR
    );

    public TOCClassifier() {
        //read the model
        try {
            trainingModel = (TrainingDocumentAnnotatingModel) objectPersistUtil.readObject(null, modelName);
        } catch (Throwable e) {
            logger.warn("TrainingDocumentAnnotatingModel is not found. creating new one");
            trainingModel = null;
        }
        if (trainingModel == null) {
            trainingModel = new TrainingDocumentAnnotatingModel( wordType,
                     wordFeatures,
                     paraType,
                     paraFeatures,
                     paraDocFeatures,
                     docFeatures);
        }
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

        ProbabilityDocumentAnnotatingModel bniModel = new ProbabilityDocumentAnnotatingModel(trainingModel.getTnbfModel(),
                trainingModel.getHmm(), document, wordType, wordFeatures, paraType, paraFeatures, paraDocFeatures, docFeatures
        );

        logger.debug("TOC before annotate {}", DocumentHelper.getTOCLists(document));

        bniMap.put(documentId, bniModel);
        bniModel.annotateDocument();

        logger.debug("TOC after annotate {} ", DocumentHelper.getTOCLists(document));
        return document;
    }

    @Override
    public Object updateBNI(String documentId, Document document, List<CoreMap> observedParas) throws Exception {
        logger.debug("TOC before annotate :");
        for (CoreMap para: DocumentHelper.getTOCParagraphs(document)){
            logger.debug(para.getId() +"\t" +DocumentHelper.getTOCLists(para).toString());
            logger.debug("IsTOCAnnotation" +"\t" +para.get(CoreAnnotations.IsTOCAnnotation.class));
        }
        logger.debug("observedParas:" + "\t" + observedParas);

        for (CoreMap para: observedParas) {
            logger.debug("observedParas:IsTOCAnnotation" + "\t" + para.get(CoreAnnotations.IsTOCAnnotation.class));
        }
        if (documentId == null || bniMap.get(documentId) == null) {
            logger.error("Document Id is NULL or BNI is return null");
            throw new Exception("Failed to updateBNI. check documentId : " + documentId);
        }
        bniMap.get(documentId).updateBeliefWithObservation(observedParas);
        bniMap.get(documentId).annotateDocument();

        printBelieves(bniMap.get(documentId), document );
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
        System.out.println("document level feature believes\n");
        double[][] dBelieves = model.getDocumentFeatureBelief();
        for (int i=0; i<dBelieves.length; i++){
            System.out.print(" " + model.DEFAULT_DOCUMENT_FEATURES);
            System.out.print(Arrays.toString(dBelieves[i]));
        }

        List<CoreMap> paraList = doc.getParagraphs();

        System.out.println("document level feature believes\n");
        double[][] pBelieves = model.getParagraphCategoryBelief();

        for (int i=0; i<paraList.size(); i++){
            BNInference.normalizeLog(pBelieves[i]);

            System.out.print(i + " [");
            for (int j=0; j<pBelieves[i].length; j++)
                System.out.printf("%.0f ", pBelieves[i][j]);
            System.out.println("] ");
            System.out.println(paraList.get(i).getText());

        }
    }
}
