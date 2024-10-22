package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.util.ParaHelper;
import com.skroll.util.WordHelper;

import java.util.List;

/**
 * Computes if a given paragraph has the first word as a number
 * <p>
 * Created by saurabh on 7/12/15.
 */
public class EndsWithAlphaWordComputer implements RVValueComputer {
    int numVals = 2;

    public EndsWithAlphaWordComputer() {

    }

    public EndsWithAlphaWordComputer(int numVals) {
        this.numVals = numVals;
    }

    // only need to check the original paragraph
    public int getValue(List<CoreMap> m) {
        return ( RVValues.booleanToInt( !ParaHelper.getLastAlphaWord(m.get(0)).equals("")) );
    }

    @Override
    public int getNumVals() {
        return numVals;
    }
}
