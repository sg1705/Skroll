package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.util.WordHelper;

import java.util.List;

/**
 * returns the last alphanumberic word
 * Created by wei on 5/9/15.
 */
public class LastWordComputer implements RVWordsComputer {

    public String[] getWords(CoreMap m) {
        List<Token> tokens = m.getTokens();
        int n = tokens.size();
        if (m == null || n == 0) return new String[0];
        for (int i = n - 1; i > 0; i--) {
            String word = tokens.get(i).getText();
            if (WordHelper.isAlphanumeric(word)) return new String[]{word.toLowerCase()};
        }
        return new String[0];
    }

    public String[] getWords(CoreMap m, int n) {
        return getWords(m);
    }
}
