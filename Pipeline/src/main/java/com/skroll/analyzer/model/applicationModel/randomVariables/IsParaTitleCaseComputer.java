package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.util.ParaHelper;

/**
 * Computes if a given paragraph has the first word as a number
 * <p>
 * Created by saurabh on 7/12/15.
 */
public class IsParaTitleCaseComputer implements RVValueComputer {
    int numVals = 2;

    public IsParaTitleCaseComputer() {

    }

    public IsParaTitleCaseComputer(int numVals) {
        this.numVals = numVals;
    }

    public int getValue(CoreMap m) {

        // for simplicity and efficiency, testing only the last word.
        String lastWord = ParaHelper.getLastAlphaWord(m);
        if (lastWord == null || lastWord.length() == 0) return 0;

        return RVValues.booleanToInt(Character.isUpperCase(lastWord.charAt(0)));

    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}
