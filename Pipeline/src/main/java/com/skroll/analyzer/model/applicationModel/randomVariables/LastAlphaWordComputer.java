package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.util.ParaHelper;

/**
 * returns the last alphanumberic word
 * Created by wei on 5/9/15.
 */
public class LastAlphaWordComputer implements RVWordsComputer {

    public String[] getWords(CoreMap m) {
        return new String[]{ParaHelper.getLastAlphaWord(m).toLowerCase()};
    }

    public String[] getWords(CoreMap m, int n) {
        return getWords(m);
    }
}
