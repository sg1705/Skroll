package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.google.common.collect.Lists;
import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ParaStartsWithFeatureComputerTest {

    ParaStartsWithFeatureComputer nTC;
    CoreMap m = new CoreMap();
    Token token1;
    Token token2;
    Token token3;

    @Before
    public void setUp() throws Exception {
        nTC = new ParaStartsWithFeatureComputer(CoreAnnotations.IsBoldAnnotation.class);
        m = new CoreMap();
        token1 = new Token("First");
        token1.set(CoreAnnotations.IsBoldAnnotation.class, true);
        token2 = new Token("token");
        token2.set(CoreAnnotations.IsBoldAnnotation.class, false);
        token3 = new Token("only");
        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token1, token2, token3));
    }

    @Test
    public void testGetValue() throws Exception {
        int value = nTC.getValue(m);
        assert (value == 1);

        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token2, token1, token3));
        value = nTC.getValue(m);
        assert (value == 0);
    }

    @Test
    public void testGetNumVals() throws Exception {
        int value = nTC.getNumVals();
        assert (value == 2);
    }
}