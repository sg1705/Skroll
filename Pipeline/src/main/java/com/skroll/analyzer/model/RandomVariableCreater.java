package com.skroll.analyzer.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by wei on 5/10/15.
 */
public class RandomVariableCreater {

    public static final Logger logger = LoggerFactory.getLogger(RandomVariableCreater.class);
    static final int DEFAULT_NUM_INT_VALS = 10;

    public static RandomVariable createParagraphStartsWithRV(Class wordAnnotation) {
        ParaStartsWithFeatureComputer computer = new ParaStartsWithFeatureComputer(wordAnnotation);
        RandomVariable rv = new RandomVariable(2, "para" + wordAnnotation.getSimpleName());
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


    static RandomVariable createRVFromAnnotation(Class ann, int numValues) {
        RandomVariable rv = new RandomVariable(numValues, ann.getName());
        RVValues.addAnnotationLink(rv, ann);
        return rv;
    }

    static RandomVariable createRVFromAnnotation(Class ann) {
        Class c = annotationType(ann);
        if (c.equals(Boolean.class)) {
            return createRVFromAnnotation(ann, 2);
        } else if (c.equals(Integer.class)) {
            return createRVFromAnnotation(ann, DEFAULT_NUM_INT_VALS);
        }
        return null; // not able to create RV automatically
    }

    static RandomVariable createRVWithComputer(RVValueComputer computer, String name) {
        RandomVariable rv = new RandomVariable(computer.getNumVals(), name);
        RVValues.addValueComputer(rv, computer);
        return rv;
    }
}


