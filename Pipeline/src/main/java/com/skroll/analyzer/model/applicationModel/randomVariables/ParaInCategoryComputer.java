package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CategoryAnnotationHelper;

import java.util.List;

/**
 * Created by wei on 5/9/15.
 */
public class ParaInCategoryComputer implements RVValueComputer {

    int categoryId;

    public ParaInCategoryComputer(int categoryId) {
        this.categoryId=categoryId;
    }

    // use the orignal para.
    public int getValue(List<CoreMap> ms){
        return getValue(ms.get(0));
    }


    public int getValue(CoreMap m) {
        return RVValues.booleanToInt(CategoryAnnotationHelper.isParagraphAnnotatedWithCategoryId(m, categoryId));

    }

    @Override
    public int getNumVals() {
        return 2;
    }
}
