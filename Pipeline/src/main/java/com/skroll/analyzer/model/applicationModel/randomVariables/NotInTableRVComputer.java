package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;

/**
 * Created by wei on 5/16/15.
 */
public class NotInTableRVComputer implements RVValueComputer {
    public int getValue(CoreMap m) {
        return 1 - RVValues.booleanToInt(m.get(CoreAnnotations.IsInTableAnnotation.class));
    }

    public int getNumVals() {
        return 2;
    }
}
