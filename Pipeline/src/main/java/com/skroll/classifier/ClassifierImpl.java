package com.skroll.classifier;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.Configuration;
import com.skroll.util.ObjectPersistUtil;

import java.util.*;

/**
 * Created by saurabhagarwal on 1/18/15.
 */
public abstract class ClassifierImpl implements Classifier {
    // Initialize the list of category this classifier support.
    protected final ArrayList<Category> categories = new ArrayList<Category>();
    Configuration configuration = new Configuration();
    private String modelFolderName = configuration.get("modelFolder","/tmp");
    protected ObjectPersistUtil objectPersistUtil = new ObjectPersistUtil(modelFolderName);

    public List<String> extractTokenFromDoc(Document doc) {
        List<CoreMap> paragraphs = doc.getParagraphs();
        Set<String> words = new HashSet<String>();
        for (CoreMap paragraph : paragraphs) {
            List<Token> tokens = paragraph.get(CoreAnnotations.TokenAnnotation.class);
            for (Token token : tokens) {
                words.add(token.getText());
            }
        }
        return new ArrayList<String>(words);
    }
    @Override
    public Object updateBNI(String documentId, Document document, List<CoreMap> observedParas) throws Exception {
        return null;
    }

    public void trainWithWeight(Document doc){};

    @Override
    public Object classify(String documentId, Document doc) throws Exception{
        return null;
    }

    public HashMap<String, HashMap<String, Double>> getBNIVisualMap(String documentId, int paraIndex) {
        return null;
    }
    public HashMap<String, HashMap<String, HashMap<String, Double>>> getModelVisualMap(String documentId) {return null;}
    public List<Double> getProbabilityDataForDoc(String documentId) {return null;}
}