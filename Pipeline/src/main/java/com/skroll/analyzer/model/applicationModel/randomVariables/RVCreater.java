package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.analyzer.model.RandomVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by wei on 5/10/15.
 */
public class RVCreater {

    public static final Logger logger = LoggerFactory.getLogger(RVCreater.class);
    static final int DEFAULT_NUM_INT_VALS = 10;

    public static RandomVariable createParagraphStartsWithRV(Class wordAnnotation) {
        ParaStartsWithFeatureComputer computer = new ParaStartsWithFeatureComputer(wordAnnotation);
        RandomVariable rv = new RandomVariable(2, "paraStarts" + wordAnnotation.getSimpleName());
        RVValues.addValueComputer(rv, computer);
        return rv;
    }

    static Class annotationType(Class ann) {
        Class c = null;
        try {
            Constructor constructor = ann.getConstructor(new Class[]{});
            Object myObject = constructor.newInstance();
            Method method = myObject.getClass().getMethod("getType");
            c = (Class) method.invoke(myObject);

        } catch (Exception ex) {
            logger.error("reflection error!");
        }
        return c;

    }

    // assuming all binary variables.
    public static List<RandomVariable> createDocFeatureRVs(List<RandomVariable> paraDocFeatures) {
        List<RandomVariable> docFeatures = new ArrayList<>();
        for (RandomVariable rv : paraDocFeatures) {
            docFeatures.add(new RandomVariable(2, "docFeature_" + rv.getName()));
        }
        return docFeatures;
    }


    static RandomVariable createRVFromAnnotation(Class ann, int numValues) {
        RandomVariable rv = new RandomVariable(numValues, ann.getName());
        RVValues.addAnnotationLink(rv, ann);
        return rv;
    }

    public static RandomVariable createRVFromAnnotation(Class ann) {
        Class c = annotationType(ann);
        if (c.equals(Boolean.class)) {
            return createRVFromAnnotation(ann, 2);
        } else if (c.equals(Integer.class)) {
            return createRVFromAnnotation(ann, DEFAULT_NUM_INT_VALS);
        } else if (c.equals(Set.class))
            return createRVFromAnnotation(ann, 0);
        return null; // not able to create RV automatically
    }

    public static RandomVariable createDiscreteRVWithComputer(RVValueComputer computer, String name) {
        RandomVariable rv = new RandomVariable(computer.getNumVals(), name);
        RVValues.addValueComputer(rv, computer);
        return rv;
    }

    public static RandomVariable createWordsRVWithComputer(RVWordsComputer computer, String name) {
        RandomVariable rv = new RandomVariable(0, name);
        RVValues.addWordsComputer(rv, computer);
        return rv;
    }


    public static RandomVariable createWordLevelRVWithComputer(WRVValueComputer computer, String name) {
        RandomVariable rv = new RandomVariable(computer.getNumVals(), name);
        RVValues.addWRVValueComputer(rv, computer);
        return rv;
    }
}


