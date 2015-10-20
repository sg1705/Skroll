package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.util.WordHelper;

import java.util.List;

/**
 * Computes if a given paragraph has the first word as a number
 * <p>
 * Created by saurabh on 7/12/15.
 */
public class IsParaNumberComputer implements RVValueComputer {
    int numVals = 2;

    public IsParaNumberComputer() {

    }

    public IsParaNumberComputer(int numVals) {
        this.numVals = numVals;
    }

    public int getValue(CoreMap m) {
        //get first token
        List<Token> tokens = m.getTokens();
        if (tokens == null || tokens.size() != 1) return 0;
        Token token = tokens.get(0);
        //logic to check if a number
        return RVValues.booleanToInt(WordHelper.isInt(token.getText()));

    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}
