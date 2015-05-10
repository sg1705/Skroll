package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;

/**
 * Created by wei on 5/9/15.
 */
public class NumberTokensComputer implements RVValueComputer {
    public int getValue(CoreMap m) {
        return m.getTokens().size();

    }
}
