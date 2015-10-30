package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;

import java.util.List;

/**
 * Created by wei on 5/16/15.
 */
public interface WRVValueComputer {
    public int getValue(Token token, List<CoreMap> para);
    public int getNumVals();
}
