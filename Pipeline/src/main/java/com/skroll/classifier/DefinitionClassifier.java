package com.skroll.classifier;

import com.google.gson.reflect.TypeToken;
import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.pipeline.util.Constants;
import com.skroll.util.ObjectPersistUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedMap;

/**
 * Created by saurabhagarwal on 1/18/15.
 */
public class DefinitionClassifier extends ClassifierImpl{

    private NaiveBayes nbModelForDefinitions = null;
    private  String modelName = "com.skroll.analyzer.model.nb.NaiveBayes.DefinitionNB";

    public DefinitionClassifier() {
        categories.add(new Category(Constants.CATEGORY_POSITIVE, "CATEGORY_POSITIVE"));
        categories.add(new Category(Constants.CATEGORY_NEGATIVE, "CATEGORY_NEGATIVE"));
        try {
            type = new TypeToken<NaiveBayes>() {}.getType();
            nbModelForDefinitions = (NaiveBayes) objectPersistUtil.readObject(type,modelName); //"com.skroll.analyzer.model.nb.NaiveBayes.secDocumentNB.1");
            System.out.println("nbModelForDefinitions" + nbModelForDefinitions);

        } catch (Throwable e) {
            e.printStackTrace();
            nbModelForDefinitions=null;
        }
        if (nbModelForDefinitions==null) {

            nbModelForDefinitions = new NaiveBayes(2, new int[]{2, Constants.DEFINITION_CLASSIFICATION_NAIVE_BAYES_NUMBER_TOKENS_USED+1});
        }

    }
    @Override
    public void persistModel() throws ObjectPersistUtil.ObjectPersistException {
        objectPersistUtil.persistObject(type,nbModelForDefinitions,modelName);
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

    }

    @Override
    public void train(Category category, Document doc) {


    }

    @Override
    public void train(Category category, String fileName, int numOfLines) {

    }

    @Override
    public void train(List<DataTuple> dataTuple) {

    }

    @Override
    public SortedMap<Category, Double> classifyDetailed(Document doc, int numOfTokens) {
        return null;
    }

    @Override
    public int classify(Document doc, int numOfTokens) {
        return 0;
    }

    @Override
    public int classify(String fileName, int numOfLines) {
        return 0;
    }


}
