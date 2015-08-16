package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Convert the text stored in tokens to lower case and return the text.
 * Created by wei on 5/9/15.
 */
public class LowerCaseWordsComputer implements RVWordsComputer {

    public String[] getWords(CoreMap m) {
        return getWords(m, Integer.MAX_VALUE);
    }

    public String[] getWords(CoreMap m, int n) {
        Set<String> wordSet = new HashSet<>();
        List<Token> tokens = m.getTokens();
        if (tokens == null) return null;
//        String[] words = new String[tokens.size()];
        int len = Math.min(n, tokens.size());
        for (int i = 0; i < len; i++) {
            wordSet.add(tokens.get(i).getText().toLowerCase());
        }
        return wordSet.toArray(new String[wordSet.size()]);
    }
}
