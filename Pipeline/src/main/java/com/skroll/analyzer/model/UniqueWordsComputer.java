package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;

import java.util.ArrayList;
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
        for (Token token : tokens)
            wordSet.add(token.toString());

        return wordSet.toArray(new String[wordSet.size()]);


    }
}
