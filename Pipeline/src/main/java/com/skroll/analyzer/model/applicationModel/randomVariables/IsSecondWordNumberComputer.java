package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.util.WordHelper;

import java.util.List;

/**
 * Computes if a given paragraph has the first word as a number
 *
 * Created by saurabh on 7/12/15.
 */
public class IsSecondWordNumberComputer implements RVValueComputer {
    int numVals = 2;

    public IsSecondWordNumberComputer() {

    }

    public IsSecondWordNumberComputer(int numVals) {
        this.numVals = numVals;
    }


    // todo: use original or processed para?
    public int getValue(List<CoreMap> ms){
        return getValue(ms.get(0));
    }

    public int getValue(CoreMap m) {

        List<Token> tokens = m.getTokens();
        if (tokens == null || tokens.size() < 2) return 0;
        Token token = tokens.get(1);
        //logic to check if a number
        return RVValues.booleanToInt(WordHelper.isInt(token.getText()));

    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}
