package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wei on 5/25/15.
 */
public class RVCreaterTest {

    @Test
    public void testCreateParagraphStartsWithRV() throws Exception {

    }


    @Test
    public void testCreateDocFeatureRVs() throws Exception {

    }

    @Test
    public void testCreateRVFromAnnotation() throws Exception {

    }

    @Test
    public void testCreateRVFromAnnotation1() throws Exception {

    }

    @Test
    public void testCreateDiscreteRVWithComputer() throws Exception {

    }

    @Test
    public void testCreateWordsRVWithComputer() throws Exception {

    }

    @Test
    public void testCreateWordLevelRVWithComputer() throws Exception {

    }

    @Test
    public void testAnnotationType() throws Exception {
        Class ac = CoreAnnotations.IndexInteger.class;
        System.out.println(RVCreater.annotationType(ac));
        assert (RVCreater.annotationType(ac)).equals(Integer.class);
    }

    @Test
    public void testCreateRVFromAnnotationInteger() throws Exception {
        int featureSize = 5;
        Class ac = CoreAnnotations.IndexInteger.class;
        RandomVariable rv = RVCreater.createRVFromAnnotation(ac, featureSize);
        assert (rv.getFeatureSize() == featureSize);
        System.out.println(rv.getName());
        System.out.println(rv.getFeatureSize());

        Class ac2 = RVValues.getAnnotationClass(rv);
        System.out.println(ac2);
        assert (ac.equals(ac2));

    }

    @Test
    public void testCreateRVFromAnnotationBoolean() throws Exception {
        Class ac = CoreAnnotations.IsBoldAnnotation.class;
        RandomVariable rv = RVCreater.createRVFromAnnotation(ac);
        assert (rv.getFeatureSize() == 2);
        System.out.println(rv.getName());
        System.out.println(rv.getFeatureSize());

        Class ac2 = RVValues.getAnnotationClass(rv);
        System.out.println(ac2);
        assert (ac.equals(ac2));


    }

    @Test
    public void testCreateRVFromAnnotationIntegerDefault() throws Exception {

        Class ac = CoreAnnotations.IndexInteger.class;
        RandomVariable rv = RVCreater.createRVFromAnnotation(ac);
        assert (rv.getFeatureSize() == RVValues.DEFAULT_NUM_INT_VALS);
        System.out.println(rv.getName());
        System.out.println(rv.getFeatureSize());

        Class ac2 = RVValues.getAnnotationClass(rv);
        System.out.println(ac2);
        assert (ac.equals(ac2));
    }


}