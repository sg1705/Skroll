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
    int categoryId;
    Class categoryAnnotations;

    public RVValueSetter(int categoryId, Class categoryAnnotations) {
        this.categoryId = categoryId;
        this.categoryAnnotations = categoryAnnotations;
    }

    // assuming value represent boolean. 0== false, 1 ==true
    void setValue(int value, CoreMap m, List<List<Token>> terms) {
        CategoryAnnotationHelper.setDInCategoryAnnotation(m, terms, categoryId);
    }

    void addTerms(CoreMap m, List<Token> terms) {
        CategoryAnnotationHelper.addDefinedTokensInCategoryAnnotation(m,terms, categoryId);
    }

    void clearValue(CoreMap m) {
        CategoryAnnotationHelper.clearCategoryAnnotation(m,categoryId);
    }
}
