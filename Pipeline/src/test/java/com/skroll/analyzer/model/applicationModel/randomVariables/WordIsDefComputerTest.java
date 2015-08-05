package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.google.common.collect.Lists;
import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.*;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordIsDefComputerTest {
    public static final Logger logger = LoggerFactory.getLogger(WordIsDefComputerTest.class);
    static final List<Integer> TEST_DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE, Category.DEFINITION));

    WordIsInCategoryComputer wordIsDefComputer;
    CoreMap m = null;
    Token token1;
    Token token2;
    Token token3;

    @Before
    public void setUp() throws Exception {
        ManagedCategoryStrategy managedCategoryStrategy = new DefaultManagedCategoryStrategy();
        UnManagedCategoryStrategy unManagedCategoryStrategy = new DefaultUnManagedCategoryStrategy();
        ModelClassAndWeightStrategy modelClassAndWeightStrategy = new DefaultModelClassAndWeightStrategy(managedCategoryStrategy, unManagedCategoryStrategy);
        wordIsDefComputer = new WordIsInCategoryComputer(modelClassAndWeightStrategy, TEST_DEF_CATEGORY_IDS);
        m = new CoreMap();
        token1 = new Token("First");
        token2 = new Token("token");
        token3 = new Token("only");
        List<Token> tokenList = Lists.newArrayList(token1,token2,token3);
        List<List<Token>> tokens = new ArrayList<>();
        tokens.add(tokenList);
        CategoryAnnotationHelper.annotateParagraphWithTokensListAndCategory(m, tokens, Category.DEFINITION);
    }
    @Test
    public void testGetValue() throws Exception {
        logger.info("{}",CategoryAnnotationHelper.getTokenStringsForCategory(m, Category.DEFINITION));;
        wordIsDefComputer.getValue(token1,m);
        assert(wordIsDefComputer.getValue(token1,m)==1);
    }

    @Test
    public void testGetNumVals() throws Exception {
      assert(wordIsDefComputer.getNumVals()==2);
    }
}