package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by wei on 5/9/15.
 */
public class FirstWordsComputer implements RVWordsComputer {

    public String[] getWords(CoreMap m) {
        return new String[]{m.getTokens().get(0).getText()};
    }

    public String[] getWords(CoreMap m, int n) {
        return new String[]{m.getTokens().get(0).getText()};
    }
}
