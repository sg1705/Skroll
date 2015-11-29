package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.util.WordHelper;

import java.util.List;

/**
 * Computes if a given paragraph has the second word starts with a digit
 * This is a simple hack to take care of the case of section 1.3.3 or 1.A for example, where the second word is not a number.
 *
 * Created by wei  on 7/12/15.
 */
public class IsSecondWordStartsWithNumberComputer implements RVValueComputer {
    int numVals = 2;

    public IsSecondWordStartsWithNumberComputer() {

    }

    public IsSecondWordStartsWithNumberComputer(int numVals) {
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
        char c =token.getText().charAt(0);
        return RVValues.booleanToInt( c > 0x30 && c < 0x39 );

    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}