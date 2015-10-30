package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;

import java.util.List;

/**
 * Created by wei on 5/9/15.
 */
public interface RVValueComputer {
    int getValue(List<CoreMap> m);

    public int getNumVals();
}
