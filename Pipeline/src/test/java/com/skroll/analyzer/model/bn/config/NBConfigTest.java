package com.skroll.analyzer.model.bn.config;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.FirstWordsComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.LowerCaseWordsComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.ParaInCategoryComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.classifier.Category;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class NBConfigTest {

    NBConfig config;
    List<RandomVariable> featureVarList;
    List<RandomVariable> wordVarList;
    RandomVariable categoryVar;

    @Before
    public void setUp() throws Exception {

        categoryVar =  RVCreater.createDiscreteRVWithComputer(new ParaInCategoryComputer(Category.DEFINITION), "paraTypeIsCategory-" + Category.DEFINITION);
        ;

        featureVarList = Lists.newArrayList(Lists.newArrayList(
                RVCreater.createRVFromAnnotation(CoreAnnotations.IsBoldAnnotation.class),
                RVCreater.createRVFromAnnotation(CoreAnnotations.IsItalicAnnotation.class),
                RVCreater.createParagraphStartsWithRV(CoreAnnotations.StartsWithQuote.class)));

        wordVarList = Arrays.asList(
                RVCreater.createWordsRVWithComputer(new LowerCaseWordsComputer(), "lowerCaseWords"),
                RVCreater.createWordsRVWithComputer(new FirstWordsComputer(), "firstWord")
        );

        config = new NBConfig(categoryVar, featureVarList, wordVarList);


    }

    @Test
    public void testGetCategoryVar() throws Exception {
        RandomVariable var = config.getCategoryVar();
        assert(var.getName().equals(categoryVar.getName()));
    }

    @Test
    public void testGetFeatureVarList() throws Exception {
        List<RandomVariable> var = config.getFeatureVarList();
        assert (var.size() == featureVarList.size());
        assert (var.get(0).getName().equals(featureVarList.get(0).getName()));
    }

    @Test
    public void testGetWordVarList() throws Exception {
        List<RandomVariable> var = config.getWordVarList();
        assert (var.size() == wordVarList.size());
        assert (var.get(0).getName().equals(wordVarList.get(0).getName()));

    }
}