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

    public int getValue(List<CoreMap> ms){
        return getValue(ms.get(0));
    }




    /**
     *
     * @param para
     * @return
     */
    public int getValue(CoreMap para) {
        List<Token> tokens = para.getTokens();
        if (tokens.size() == 0) return 0;
        Token firstToken = tokens.get(0);

        // for efficiency, checking only the first and the last token.
        return RVValues.booleanToInt(para.getTokens().get(0).<Boolean>get(wordFeature)) &
                RVValues.booleanToInt(para.getTokens().get(tokens.size() - 1).<Boolean>get(wordFeature)
                );
    }

    public int getNumVals() {
        return 2;
    }
}
