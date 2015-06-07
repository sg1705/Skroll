package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.List;

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
//        int n = m.getTokens().size();
//        return n < numVals ? n : numVals - 1;
        // alternative way to get the number of tokens using token index,
        // to avoid the problem of original para has more tokens then processed para,
        // and so the original token number is used.
        List<Token> tokens = m.getTokens();
        if (tokens == null || tokens.size() == 0) return 0;
        Token lastToken = tokens.get(tokens.size() - 1);
        Integer lastIndex = lastToken.get(CoreAnnotations.IndexInteger.class);
        if (lastIndex == null) return -1;
        int n = lastIndex + 1; //n is now at least 1, numVals should be at least 2.
        return n < numVals ? n : numVals - 1;

    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}
