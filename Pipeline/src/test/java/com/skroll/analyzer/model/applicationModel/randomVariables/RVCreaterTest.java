package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/25/15.
 */
public class RVCreaterTest {

    public static final Logger logger = LoggerFactory.getLogger(RVCreaterTest.class);
    static final List<Integer> TEST_DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE, Category.DEFINITION));


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
        List<RandomVariable> rvList = RVCreater.createDocFeatureRVs(Lists.newArrayList(rv1, rv2), "def");
        assert(rvList.size()==2);
        logger.info("{}", rvList);
        assert (rvList.get(0).getName().equals("def_paraStartsStartsWithQuote"));
        assert (rvList.get(1).getName().equals("def_paraStartsIsBoldAnnotation"));
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
        FirstWordComputer fWC;
        CoreMap m = new CoreMap();
        Token token1;
        Token token2;
        Token token3;
        fWC = new FirstWordComputer();
        token1 = new Token("First");
        token2 = new Token("token");
        token3 = new Token("only");
        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token1, token2, token3));
        RandomVariable rv = RVCreater.createWordsRVWithComputer(fWC, "FirstWordComputer");
        logger.info("{}", rv);
        logger.info("{}", RVValues.getWords(rv, m));
        assert (rv.getName().equals("FirstWordComputer"));
        assert(RVValues.getWords(rv, m)[0].equals("First".toLowerCase()));
    }

    @Test
    public void testCreateWordLevelRVWithComputer() throws Exception {
        ManagedCategoryStrategy managedCategoryStrategy = new DefaultManagedCategoryStrategy();
        UnManagedCategoryStrategy unManagedCategoryStrategy = new DefaultUnManagedCategoryStrategy();
        ModelClassAndWeightStrategy modelClassAndWeightStrategy = new DefaultModelClassAndWeightStrategy(managedCategoryStrategy, unManagedCategoryStrategy);

        RandomVariable rv = RVCreater.createWordLevelRVWithComputer(new WordIsInCategoryComputer(modelClassAndWeightStrategy,TEST_DEF_CATEGORY_IDS), "WordIsTOCComputer");
        logger.info("{}", rv);
        assert(rv.getName().equals("WordIsTOCComputer"));
        assert(rv.getFeatureSize()==2);
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
        assert (rv.getFeatureSize() == RVCreater.DEFAULT_NUM_INT_VALS);
        System.out.println(rv.getName());
        System.out.println(rv.getFeatureSize());

        Class ac2 = RVValues.getAnnotationClass(rv);
        System.out.println(ac2);
        assert (ac.equals(ac2));
    }


}