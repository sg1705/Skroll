package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.applicationModel.ParaProcessor;
import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NumberTokensComputerTest {

    NumberTokensComputer nTC;
    CoreMap m = new CoreMap();
    Token token1;
    Token token2;
    Token token3;

    @Before
    public void setUp() throws Exception {
        nTC = new NumberTokensComputer();
        m = new CoreMap();
        token1 = new Token("First");
        token2 = new Token("token");
        token3 = new Token("only");
        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token1, token2, token3));
        m.set(CoreAnnotations.IsInTableAnnotation.class, true);

    }

    @Test
    public void testGetValue() throws Exception {
        assert (nTC.getNumVals() == RVCreater.DEFAULT_NUM_INT_VALS);
        m = ParaProcessor.processParagraph(m);
        int value = nTC.getValue(m);
        System.out.println(value);
        assert(value == 3);

        m = new CoreMap();
        token1 = new Token("First");
        token2 = new Token("token");
        token3 = new Token("only");
        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token1, token2, token3, token1, token2, token3, token1, token2, token3, token1, token2, token3, token1, token2, token3));

        m = ParaProcessor.processParagraph(m);
        value = nTC.getValue(m);
        System.out.println(value);
        assert (value == 15);
    }

}