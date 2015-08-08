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

    protected List<Integer> categoryIds;


    public RVValueSetter(List<Integer> categoryIds, Class Annotation) {
        this.categoryIds = categoryIds;

    }

    // assuming value represent boolean. 0== false, 1 ==true
    void setValue(int value, CoreMap m, List<List<Token>> terms) {
        CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategoryOfClassIndex(m, terms, categoryIds, value); // need one more para seqId to set the right category for the terms.
    }

    void addTerms(CoreMap m, List<Token> terms, int value) {
        CategoryAnnotationHelper.annotateParagraphWithTokensAndCategoryOfClassIndex(m, terms, categoryIds, value); //same here

    }

    void clearValue(CoreMap m) {
        CategoryAnnotationHelper.clearCategoryAnnotations(m); //same here
    }
}
