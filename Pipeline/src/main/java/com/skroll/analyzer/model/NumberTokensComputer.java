package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;

/**
 * Created by wei on 5/9/15.
 */
public class NumberTokensComputer implements RVValueComputer {
    int numVals = RVCreater.DEFAULT_NUM_INT_VALS;

    public NumberTokensComputer() {

    }

    public NumberTokensComputer(int numVals) {
        this.numVals = numVals;
    }
    public int getValue(CoreMap m) {
        int n = m.getTokens().size();
        return n < numVals ? n : numVals;

    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}
