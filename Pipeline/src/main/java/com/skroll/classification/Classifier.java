package com.skroll.classification;

import com.skroll.analyzer.model.nb.DataTuple;
import com.skroll.classification.category.Category;
import com.skroll.document.Document;

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

    public  void train(Category category, Document doc);

    void train(Category category, String fileName, int numOfLines);

    public  void train(List<DataTuple> dataTuple);

    public  SortedMap<Category,Double> classifyDetailed(Document doc, int numOfTokens);

    public int classify(Document doc, int numOfTokens);

    int classify(String fileName, int numOfLines);
}
