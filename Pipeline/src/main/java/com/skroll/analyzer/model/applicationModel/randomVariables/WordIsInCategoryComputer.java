package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;

import java.util.List;

/**
 * Created by wei on 5/16/15.
 */
public class WordIsInCategoryComputer implements WRVValueComputer {

    protected int categoryId;

    public WordIsInCategoryComputer(int categoryId){
        this.categoryId=categoryId;
    }
    @Override
    public int getValue(Token word, CoreMap para) {
        List<List<Token>> tokens = CategoryAnnotationHelper.getDefinedTermTokensInParagraph(para, categoryId);
        if (tokens == null) return 0;

        for (List<Token> list : tokens)
            for (Token t : list) //todo: matching string instead of matching token reference is a hack here.
                if (t.getText().equals(word.getText())) return 1;
        //if (list.contains(word)) return 1;
        return 0;
    }


    @Override
    public int getNumVals() {
        return 2;
    }
}