package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotation;
import com.skroll.document.annotation.CoreAnnotations;
import com.skroll.util.ParaHelper;

import java.util.List;

/**
 * Computes if a given paragraph is Title case.
 * Just checking if the last word is title case.
 * todo: may need to check how good is this.
 * Created by wei on 7/12/15.
 */
public class IsParaTitleCaseComputer implements RVValueComputer {
    int numVals = 2;

    public IsParaTitleCaseComputer() {

    }

    public IsParaTitleCaseComputer(int numVals) {
        this.numVals = numVals;
    }

    // only check the original paragraph
    public int getValue(List<CoreMap> m) {

        // for simplicity and efficiency, testing only the last word.
        String lastWord = ParaHelper.getLastAlphaWord(m.get(0));
        if (lastWord == null || lastWord.length() == 0) return -1;

        // return unobserved if It there is only one word.
        if (m.get(0).getTokens().size() == 1) return -1;

        return RVValues.booleanToInt(Character.isUpperCase(lastWord.charAt(0)) ||
                m.get(0).get(CoreAnnotations.StartsWithUpperCaseCountInteger.class)>1);

    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}
