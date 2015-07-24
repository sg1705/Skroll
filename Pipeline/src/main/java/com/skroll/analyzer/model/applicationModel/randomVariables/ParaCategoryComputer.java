package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.classifier.ClassifierProto;
import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CategoryAnnotationHelper;

/**
 * Created by wei on 5/9/15.
 */
public class ParaCategoryComputer implements RVValueComputer {

    ClassifierProto classifierProto;

    public ParaCategoryComputer(ClassifierProto classifierProto) {
        this.classifierProto = classifierProto;
    }

    public int getValue(CoreMap m) {
        return CategoryAnnotationHelper.getObservedClassIndex(m, classifierProto);

    }

    @Override
    public int getNumVals() {
        return classifierProto.getCategoryIds().size();
    }
}
