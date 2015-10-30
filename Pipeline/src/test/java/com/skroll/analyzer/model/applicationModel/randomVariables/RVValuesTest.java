package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
import com.skroll.document.CoreMap;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/9/15.
 */
public class RVValuesTest {

    // paragraph features not exist at doc level
    public static final List<RandomVariable> paraFeatures = new ArrayList<>();

    @Before
    public void setUp() throws Exception {
        RandomVariable rvNumTokens = new RandomVariable(10, "numberTokens");
        paraFeatures.add(rvNumTokens);
//        RVValues.

    }

    @Test
    public void testGetIntValue() throws Exception {
        CoreMap para = new CoreMap();
        Class ac = CoreAnnotations.IndexInteger.class;

        para.set(ac, 3);
        RandomVariable rv = RVCreater.createRVFromAnnotation(ac);
        int v = RVValues.getValue(rv, Arrays.asList(para));
        System.out.println(v);
        assert (v == 3);


    }

    @Test
    public void testGetBooleanValue() throws Exception {
        CoreMap para = new CoreMap();
        Class ac = CoreAnnotations.IsBoldAnnotation.class;

        para.set(ac, Boolean.TRUE);
        RandomVariable rv = RVCreater.createRVFromAnnotation(ac);
        int v = RVValues.getValue(rv, Arrays.asList(para));
        System.out.println(v);
        assert (v == 1);


    }

//    @Test
//    public void testCreateRVFromAnnotationIntegerDefault() throws Exception {
//
//        Class ac = CoreAnnotations.IndexInteger.class;
//        RandomVariable rv = RVValues.createRVFromAnnotation(ac);
//        assert (rv.getFeatureSize() == RVValues.DEFAULT_NUM_INT_VALS);
//        System.out.println(rv.getName());
//        System.out.println(rv.getFeatureSize());
//
//        Class ac2 = RVValues.getAnnotationClass(rv);
//        System.out.println(ac2);
//        assert (ac.equals(ac2));
//
//    }
}