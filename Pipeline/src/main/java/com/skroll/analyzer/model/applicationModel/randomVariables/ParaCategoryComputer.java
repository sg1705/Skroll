package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CategoryAnnotationHelper;

/**
 * Created by wei on 5/9/15.
 */
public class ParaCategoryComputer implements RVValueComputer {

    int classifierId;
    int numOfCategory;

    public ParaCategoryComputer(int classifierId, int numOfCategory) {
        this.classifierId = classifierId;
        this.numOfCategory = numOfCategory;
    }

    public int getValue(CoreMap m) {
        return CategoryAnnotationHelper.getObservedClassIndex(m, classifierId);

    }

    @Override
    public int getNumVals() {
        return numOfCategory;
    }
}
