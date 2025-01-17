package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.annotation.ModelClassAndWeightStrategy;

import java.util.List;

/**
 * Created by wei on 5/9/15.
 */
public class ParaCategoryComputer implements RVValueComputer {

    protected List<Integer> categoryIds;
    ModelClassAndWeightStrategy modelClassAndWeightStrategy;

    public ParaCategoryComputer(ModelClassAndWeightStrategy modelClassAndWeightStrategy, List<Integer> categoryIds) {
        this.categoryIds = categoryIds;
        this.modelClassAndWeightStrategy = modelClassAndWeightStrategy;
    }

    // use the orignal para.
    public int getValue(List<CoreMap> m) {
        return modelClassAndWeightStrategy.getClassIndexForModel(m.get(0), categoryIds);


    }

    @Override
    public int getNumVals() {

        return categoryIds.size();
    }
}
