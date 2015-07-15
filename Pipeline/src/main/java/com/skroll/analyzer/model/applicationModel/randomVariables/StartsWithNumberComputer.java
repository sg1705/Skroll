package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;

import java.util.List;

/**
 * Computes if a given paragraph has the first word as a number
 *
 * Created by saurabh on 7/12/15.
 */
public class StartsWithNumberComputer implements RVValueComputer {
    int numVals = 2;

    public StartsWithNumberComputer() {

    }

    public StartsWithNumberComputer(int numVals) {
        this.numVals = numVals;
    }

    public int getValue(CoreMap m) {
        //get first token
        List<Token> tokens = m.getTokens();
        if (tokens == null || tokens.size() == 0) return 0;
        Token token = tokens.get(0);
        //logic to check if a number
        boolean isNumber = token.getText().matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
        if (isNumber) {
            return 1;
        } else {
            return 0;
        }

    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}
