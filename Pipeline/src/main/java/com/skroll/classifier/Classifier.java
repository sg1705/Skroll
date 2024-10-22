package com.skroll.classifier;

import com.skroll.analyzer.model.applicationModel.ModelRVSetting;
import com.skroll.document.CoreMap;
import com.skroll.document.Document;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saurabhagarwal on 1/5/15.
 */
public interface Classifier {


    //train the model that this classifier class hold so the user does not have to know anything about the model.
    // He just need what classifier class it need to talk to.
    // The classifier objects based on our different classification need ( such as definition, document classification etc)
    // will be created in the main instances of our production installation.


    public Map<String, Double> getProbabilityDataForDoc(Document document);

    public void persistModel() throws  Exception;
    public Object classify(String documentId, Document doc);
    public void trainWithWeight(Document doc);
    public void train(Document doc);

    public int getId();

    public Object updateBNI(String documentId, Document document, List<CoreMap> observedParas) throws Exception;

    public ModelRVSetting getModelRVSetting();

    public HashMap<String, HashMap<String, HashMap<String, Double>>> getBNIVisualMap(Document document, int paraIndex);

    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap();


}
