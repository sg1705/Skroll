package com.skroll.classifier;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.parser.Parser;
import com.skroll.parser.extractor.ParserException;
import com.skroll.parser.linker.DefinitionLinker;
import com.skroll.pipeline.Pipeline;
import com.skroll.pipeline.Pipes;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.ObjectPersistUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Created by saurabhagarwal on 1/18/15.
 */
public class DefinitionClassifier extends ClassifierImpl{

    public static final Logger logger = LoggerFactory
            .getLogger(DefinitionClassifier.class);

    private NaiveBayes nbModelForDefinitions = null;
    private  HiddenMarkovModel hmmModel = null;
    private  String nbModelName = "com.skroll.analyzer.model.nb.NaiveBayes.DefinitionNB";
    private  String hmmModelName = "com.skroll.analyzer.model.hmm.HiddenMarkovModel.DefinitionHMM";
    private  Type nbType = null;
    private  Type hmmType = null;

    public DefinitionClassifier() {
        categories.add(new Category(Constants.CATEGORY_POSITIVE, "CATEGORY_POSITIVE"));
        categories.add(new Category(Constants.CATEGORY_NEGATIVE, "CATEGORY_NEGATIVE"));
        try {
            nbType = new TypeToken<NaiveBayes>() {}.getType();
            nbModelForDefinitions = (NaiveBayes) objectPersistUtil.readObject(nbType,nbModelName);
            logger.debug("nbModelForDefinitions:" + nbModelForDefinitions);

        } catch (Throwable e) {
            e.printStackTrace();
            nbModelForDefinitions=null;
        }
        if (nbModelForDefinitions==null) {

            nbModelForDefinitions = new NaiveBayes(2, new int[]{2, Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS_USED+1});
        }
        try {
            hmmType = new TypeToken<HiddenMarkovModel>() {}.getType();
            hmmModel = (HiddenMarkovModel) objectPersistUtil.readObject(hmmType,hmmModelName);
            logger.debug("hmmModelName:" + hmmModel);

        } catch (Throwable e) {
            e.printStackTrace();
            hmmModel=null;
        }
        if (hmmModel==null) {

            hmmModel = new HiddenMarkovModel();
        }

    }
    @Override
    public void persistModel() throws ObjectPersistUtil.ObjectPersistException {
        objectPersistUtil.persistObject(nbType,nbModelForDefinitions,nbModelName);
        logger.debug("persisted nbModelForDefinitions:" + nbModelForDefinitions);
        objectPersistUtil.persistObject(hmmType,hmmModel,hmmModelName);
        logger.debug("persisted hmmModel:" + hmmModel);
    }

    @Override
    public void train(Document doc) {
        List<CoreMap> paragraphs = doc.getParagraphs();

        for (CoreMap paragraph : paragraphs) {
            Set<String> words = new HashSet<String>();
            List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);
            // check whether the paragraph contain the Defined Term Annotation or not?
            DataTuple tuple = null;
            for (Token token : tokens) {
                words.add(token.getText());
            }

            if (paragraph.containsKey(CoreAnnotations.IsDefinitionAnnotation.class)) {
                tuple = new DataTuple(Constants.CATEGORY_POSITIVE, words.toArray(new String[words.size()]), null);
            } else {
                tuple = new DataTuple(Constants.CATEGORY_NEGATIVE, words.toArray(new String[words.size()]), null);
            }

            nbModelForDefinitions.addSample(tuple);

        }
        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.FILTER_STARTS_WITH_QUOTE_IN_HTML_DOC)
                        .add(Pipes.HTML_HIDDEN_MARKOV_MODEL_TRAINING_PIPE,
                                Lists.newArrayList((Object) hmmModel))
                        .build();
        Document hmmDoc = pipeline.process(doc);

        hmmModel.updateProbabilities();

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

        if (hmmModel==null){
            throw new Exception("hmmModel is not defined");
        }
        if (nbModelForDefinitions==null){
            throw new Exception("nbModelForDefinitions is not defined");
        }

        Pipeline<Document, Document> pipeline =
                new Pipeline.Builder()
                        .add(Pipes.HTML_DOC_BINARY_NAIVE_BAYES_TESTER,
                                Lists.newArrayList((Object) nbModelForDefinitions))
                        .add(Pipes.HTML_DOCUMENT_HIDDEN_MARKOV_MODEL_TESTING_PIPE,
                                Lists.newArrayList((Object) hmmModel, Constants.CATEGORY_POSITIVE))
                        .build();
        document = pipeline.process(document);
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
