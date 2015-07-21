package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.classifier.ClassifierProto;
import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;

import java.util.List;

/**
 * Created by wei on 5/19/15.
 */
// may consider using interface if more types of needed
public class RVValueSetter {
    ClassifierProto classifierProto;


    public RVValueSetter(ClassifierProto classifierProto, Class Annotation) {
        this.classifierProto = classifierProto;
    }

    // assuming value represent boolean. 0== false, 1 ==true
    void setValue(int value, CoreMap m, List<List<Token>> terms) {
        CategoryAnnotationHelper.setTokensForClassifier(m, terms, classifierProto, value); // need one more para seqId to set the right category for the terms.
    }

    void addTerms(CoreMap m, List<Token> terms, int value) {
        CategoryAnnotationHelper.addTokensForClassifier(m, terms, classifierProto, value); //same here
    }

    void clearValue(CoreMap m) {
        CategoryAnnotationHelper.clearAnnotations(m); //same here
    }
}
