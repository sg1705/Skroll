package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wei on 5/9/15.
 */
public class UniqueWordsComputer implements RVWordsComputer {

    public String[] getWords(CoreMap m) {
        return getWords(m, m.getTokens().size());
    }

    // not really used for now, since the paragraphs are preprocessed to remove words in the back
    public String[] getWords(CoreMap m, int n) {
        Set<String> wordSet = new HashSet<>();
        List<Token> tokens = m.getTokens();
        for (int i = 0; i < n; i++) {
            wordSet.add(tokens.get(i).toString());
        }

        return wordSet.toArray(new String[wordSet.size()]);


    }
}
