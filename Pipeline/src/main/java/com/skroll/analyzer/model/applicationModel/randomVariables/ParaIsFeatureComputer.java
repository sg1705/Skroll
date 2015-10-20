package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;

import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class ParaIsFeatureComputer implements RVValueComputer {
    Class wordFeature;

    public ParaIsFeatureComputer(Class wordAnnotation) {
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

        // for efficiency, checking only the first and the last token.
        return RVValues.booleanToInt(processedPara.getTokens().get(0).<Boolean>get(wordFeature)) &
                RVValues.booleanToInt(processedPara.getTokens().get(tokens.size() - 1).<Boolean>get(wordFeature)
                );
    }

    public int getNumVals() {
        return 2;
    }
}
