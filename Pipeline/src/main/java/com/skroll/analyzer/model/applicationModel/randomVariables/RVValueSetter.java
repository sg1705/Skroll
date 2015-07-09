package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;

import java.util.List;

/**
 * Created by wei on 5/19/15.
 */
// may consider using interface if more types of needed
public class RVValueSetter {
    int classifierId;


    public RVValueSetter(int classifierId, Class Annotation) {
        this.classifierId = classifierId;
    }

    // assuming value represent boolean. 0== false, 1 ==true
    void setValue(int value, CoreMap m, List<List<Token>> terms) {
        CategoryAnnotationHelper.setTokensForClassifier(m, terms, classifierId, value); // need one more para seqId to set the right category for the terms.
    }

    void addTerms(CoreMap m, List<Token> terms) {
        CategoryAnnotationHelper.addTokensForClassifier(m,terms, classifierId); //same here
    }

    void clearValue(CoreMap m) {
        CategoryAnnotationHelper.clearAnnotations(m); //same here
    }
}
