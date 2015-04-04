package com.skroll.classifier;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.parser.extractor.ParserException;
import com.skroll.util.ObjectPersistUtil;

import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;

/**
 * Created by saurabhagarwal on 1/5/15.
 */
public interface Classifier {


    //train the model that this classifier class hold so the user does not have to know anything about the model.
    // He just need what classifier class it need to talk to.
    // The classifier objects based on our different classification need ( such as definition, document classification etc)
    // will be created in the main instances of our production installation.

    //TODO: rationalize the  training functions
    public void train(Document doc);

    public  void train(Category category, Document doc);

    public void train(Category category, String fileName, int numOfLines) throws ParserException;

    public void persistModel() throws ObjectPersistUtil.ObjectPersistException;

    //TODO: rationalize the  classify functions

    public Object classify(String documentId, Document doc) throws Exception;

    public Object classify(Document doc, int numOfTokens) throws Exception;

    public Object classify(String fileName, int numOfLines) throws Exception;

    public SortedMap<Category,Double> classifyDetailed(Document doc, int numOfTokens);

    public Object updateBNI(String documentId, Document document, List<CoreMap> observedParas) throws Exception;
    public void trainWithWeight(Document doc);

    public HashMap<String, HashMap<String, Double>> getBNIVisualMap(String docId, int paraIndex);
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap(String documentId);
    public List<Double> getProbabilityDataForDoc(String documentId);

}
