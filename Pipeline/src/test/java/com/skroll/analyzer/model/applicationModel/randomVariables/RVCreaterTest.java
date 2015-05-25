package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by wei on 5/25/15.
 */
public class RVCreaterTest {
    public static final Logger logger = LoggerFactory.getLogger(RVCreaterTest.class);
    @Test
    public void testCreateParagraphStartsWithRV() throws Exception {
        RandomVariable rv = RVCreater.createParagraphStartsWithRV(CoreAnnotations.StartsWithQuote.class);
        logger.info("RV.Name:{}", rv.getName());
        assert(rv.getName().equals("paraStartsStartsWithQuote"));
    }


    @Test
    public void testCreateDocFeatureRVs() throws Exception {

        RandomVariable rv1 = RVCreater.createParagraphStartsWithRV(CoreAnnotations.StartsWithQuote.class);
        RandomVariable rv2 = RVCreater.createParagraphStartsWithRV(CoreAnnotations.IsBoldAnnotation.class);
        List<RandomVariable> rvList = RVCreater.createDocFeatureRVs(Lists.newArrayList(rv1,rv2));
        assert(rvList.size()==2);
        logger.info("{}", rvList);
        assert(rvList.get(0).getName().equals("docFeature_paraStartsStartsWithQuote"));
        assert(rvList.get(1).getName().equals("docFeature_paraStartsIsBoldAnnotation"));
    }


    @Test
    public void testCreateDiscreteRVWithComputer() throws Exception {
        NumberTokensComputer numberTokensComputer = new NumberTokensComputer(10);
        RandomVariable rv = RVCreater.createDiscreteRVWithComputer(numberTokensComputer, "NumberTokensComputer");
        logger.info("{}", rv);
        assert(rv.getName().equals("NumberTokensComputer"));
        assert(rv.getFeatureSize()==10);
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