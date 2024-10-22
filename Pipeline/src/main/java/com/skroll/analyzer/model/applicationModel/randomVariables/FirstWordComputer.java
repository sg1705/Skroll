package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;

/**
 * Created by wei on 5/9/15.
 */
public class FirstWordComputer implements RVWordsComputer {

    public String[] getWords(CoreMap m) {
        if (m == null || m.getTokens().size() == 0) return new String[0];
        return new String[]{m.getTokens().get(0).getText().toLowerCase()};
    }

    public String[] getWords(CoreMap m, int n) {
        return new String[]{m.getTokens().get(0).getText().toLowerCase()};
    }
}
