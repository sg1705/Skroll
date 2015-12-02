package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.List;

/**
 * Created by wei on 5/16/15.
 */
public class NotInTableRVComputer implements RVValueComputer {

    //only check the original para.
    public int getValue(List<CoreMap> m) {
        // a hack to check if paragraph is original. Only original has IdAnnotation
//        if (!m.containsKey(CoreAnnotations.IdAnnotation.class)) return -1;

        return 1 - RVValues.booleanToInt(m.get(0).get(CoreAnnotations.IsInTableAnnotation.class));
    }

    public int getNumVals() {
        return 2;
    }
}
