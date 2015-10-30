package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.List;

/**
 * Logic to check if a paragraph matches with user defined TOC
 * Created by saurabh on 7/19/15.
 */
public class IsInUserTOCRVComputer implements RVValueComputer {

    // only need to check the processed para.
    public int getValue(List<CoreMap> m) {

        if (!m.get(1).containsKey(CoreAnnotations.IsInUserDefinedTOCAnnotation.class)) return -1;

        return RVValues.booleanToInt(m.get(1).get(CoreAnnotations.IsInUserDefinedTOCAnnotation.class));
    }

    public int getNumVals() {
        return 2;
    }
}
