package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;

import java.util.List;

/**
 * Created by wei on 5/16/15.
 */
public class WordIsTOCComputer implements WRVValueComputer {

    @Override
    public int getValue(Token word, CoreMap para) {
        List<Token> tokens = para.get(CoreAnnotations.TOCTokensAnnotation.class);
        if (tokens == null) return 0;
        for (Token t : tokens) //todo: matching string instead of matching token reference is a hack here.
            if (t.getText().equals(word.getText())) return 1;
        //if (list.contains(word)) return 1;
        return 0;
    }

    @Override
    public int getNumVals() {
        return 2;
    }
}
