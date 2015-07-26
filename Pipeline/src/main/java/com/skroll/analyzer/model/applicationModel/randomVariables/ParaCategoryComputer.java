package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CategoryAnnotationHelper;

import java.util.List;

/**
 * Created by wei on 5/9/15.
 */
public class ParaCategoryComputer implements RVValueComputer {

    protected List<Integer> categoryIds;

    public ParaCategoryComputer(List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public int getValue(CoreMap m) {
        return CategoryAnnotationHelper.getObservedClassIndex(m, categoryIds);

    }

    @Override
    public int getNumVals() {
        return categoryIds.size();
    }
}
