package com.skroll.analyzer.model;

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
        Token firstToken = tokens.get(0);
        return RVValues.booleanToInt(processedPara.getTokens().get(0).<Boolean>get(wordFeature));
    }

    public int getNumVals() {
        return 2;
    }
}
