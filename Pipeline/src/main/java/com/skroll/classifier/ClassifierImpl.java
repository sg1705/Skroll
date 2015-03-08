package com.skroll.classifier;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by saurabhagarwal on 1/18/15.
 */
public abstract class ClassifierImpl implements Classifier {
    // Initialize the list of category this classifier support.
    protected final ArrayList<Category> categories = new ArrayList<Category>();

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

}