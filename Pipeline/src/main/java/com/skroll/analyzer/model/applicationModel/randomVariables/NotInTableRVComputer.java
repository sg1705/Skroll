package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;

/**
 * Created by wei on 5/16/15.
 */
public class NotInTableRVComputer implements RVValueComputer {
    public int getValue(CoreMap m) {
        // a hack to check if paragraph is original. Only original has IdAnnotation
        if (!m.containsKey(CoreAnnotations.IdAnnotation.class)) return -1;

        return 1 - RVValues.booleanToInt(m.get(CoreAnnotations.IsInTableAnnotation.class));
    }

    public int getNumVals() {
        return 2;
    }
}
