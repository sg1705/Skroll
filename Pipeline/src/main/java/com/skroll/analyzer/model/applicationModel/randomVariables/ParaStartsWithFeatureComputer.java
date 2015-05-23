package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.analyzer.model.applicationModel.randomVariables.RVValueComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.document.CoreMap;
import com.skroll.document.Token;

import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class ParaStartsWithFeatureComputer implements RVValueComputer {
    Class wordFeature;

    public ParaStartsWithFeatureComputer(Class wordAnnotation) {
        wordFeature = wordAnnotation;
    }

    /**
     * The paragraph has to be processed to have the first token meaningful, not a quote
     *
     * @param processedPara
     * @return
     */
    public int getValue(CoreMap processedPara) {
        List<Token> tokens = processedPara.getTokens();
        if (tokens.size() == 0) return 0;
        Token firstToken = tokens.get(0);
        return RVValues.booleanToInt(processedPara.getTokens().get(0).<Boolean>get(wordFeature));
    }

    public int getNumVals() {
        return 2;
    }
}
