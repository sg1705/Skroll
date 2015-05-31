package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CategoryAnnotationHelper;

/**
 * Created by wei on 5/9/15.
 */
public class ParaInCategoryComputer implements RVValueComputer {

    int categoryId;

    public ParaInCategoryComputer(int categoryId) {
        this.categoryId=categoryId;
    }

    public int getValue(CoreMap m) {
        return RVValues.booleanToInt(CategoryAnnotationHelper.isCategoryId(m, categoryId));

    }

    @Override
    public int getNumVals() {
        return 2;
    }
}
