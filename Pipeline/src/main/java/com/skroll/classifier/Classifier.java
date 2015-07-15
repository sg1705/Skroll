package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabhagarwal on 1/5/15.
 */
public interface Classifier {


    //train the model that this classifier class hold so the user does not have to know anything about the model.
    // He just need what classifier class it need to talk to.
    // The classifier objects based on our different classification need ( such as definition, document classification etc)
    // will be created in the main instances of our production installation.


    public void trainWithWeight(Document doc);

    public void train(Document doc);

    public Object classify(String documentId, Document doc);

    public ModelRVSetting getModelRVSetting();

    public Object updateBNI(String documentId, Document document, List<CoreMap> observedParas) throws Exception;

    public HashMap<String, HashMap<String, HashMap<String, Double>>> getBNIVisualMap(Document doc, int paraIndex);

    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap();

    public List<Double> getProbabilityDataForDoc(Document doc);

    public void persistModel() throws  Exception;

    public int getClassifierId();

    public List<Integer> getCategoryIds();


}
