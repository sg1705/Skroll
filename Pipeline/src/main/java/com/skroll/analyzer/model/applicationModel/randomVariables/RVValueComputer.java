package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;

/**
 * Created by wei on 5/9/15.
 */
public interface RVValueComputer {
    int getValue(CoreMap m);

    public int getNumVals();
}
