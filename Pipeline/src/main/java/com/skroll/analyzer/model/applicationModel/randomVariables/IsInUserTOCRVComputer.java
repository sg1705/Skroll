package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.CoreAnnotations;

/**
 * Logic to check if a paragraph matches with user defined TOC
 * Created by saurabh on 7/19/15.
 */
public class IsInUserTOCRVComputer implements RVValueComputer {

    public int getValue(CoreMap m) {

        if (!m.containsKey(CoreAnnotations.IsInUserDefinedTOCAnnotation.class)) return -1;

        return RVValues.booleanToInt(m.get(CoreAnnotations.IsInUserDefinedTOCAnnotation.class));
    }

    public int getNumVals() {
        return 2;
    }
}
