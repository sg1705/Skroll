package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CategoryAnnotationHelper;
import com.skroll.document.annotation.ModelClassAndWeightStrategy;

import java.util.List;

/**
 * Created by wei on 5/16/15.
 */
public class WordIsInCategoryComputer implements WRVValueComputer {

    protected List<Integer> categoryIds;
    ModelClassAndWeightStrategy modelClassAndWeightStrategy;

    public WordIsInCategoryComputer(ModelClassAndWeightStrategy modelClassAndWeightStrategy, List<Integer> categoryIds){
        this.categoryIds=categoryIds;
        this.modelClassAndWeightStrategy = modelClassAndWeightStrategy;
    }

    // use original para.
    @Override
    public int getValue(Token word, List<CoreMap> paras){
        return getValue(word, paras.get(0));
    }

    public int getValue(Token word, CoreMap para) {
        int observedCategoryId = modelClassAndWeightStrategy.getCategoryIdForModel(para, categoryIds);
        List<List<Token>> tokens = CategoryAnnotationHelper.getTokensForCategory(para, observedCategoryId); //need one more field
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
