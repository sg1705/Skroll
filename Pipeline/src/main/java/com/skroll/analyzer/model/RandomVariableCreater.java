package com.skroll.analyzer.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Created by wei on 5/10/15.
 */
public class RandomVariableCreater {

    static final int DEFAULT_NUM_INT_VALS = 10;

    public static RandomVariable createParagraphStartsWithRV(Class wordAnnotation) {
        ParaStartsWithFeatureComputer computer = new ParaStartsWithFeatureComputer(wordAnnotation);
        RandomVariable rv = new RandomVariable(2, "para" + wordAnnotation.getSimpleName());
        RVValues.addValueComputer(rv, computer);
        return rv;
    }

    static Class annotationType(Class ann) throws Exception {
        Constructor constructor = ann.getConstructor(new Class[]{});
        Object myObject = constructor.newInstance();
        Method method = myObject.getClass().getMethod("getType");
        Object c = method.invoke(myObject);
        return (Class) c;

    }


    static RandomVariable createRVFromAnnotation(Class ann, int numValues) throws Exception {
        RandomVariable rv = new RandomVariable(numValues, ann.getName());
        RVValues.addAnnotationLink(rv, ann);
        return rv;
    }

    static RandomVariable createRVFromAnnotation(Class ann) throws Exception {
        Class c = annotationType(ann);
        if (c.equals(Boolean.class)) {
            return createRVFromAnnotation(ann, 2);
        } else if (c.equals(Integer.class)) {
            return createRVFromAnnotation(ann, DEFAULT_NUM_INT_VALS);
        }
        return null; // not able to create RV automatically
    }
}


