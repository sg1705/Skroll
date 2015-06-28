package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class NotInTableRVComputerTest {

    NotInTableRVComputer nTC;
    CoreMap m = new CoreMap();
    Token token1;
    Token token2;
    Token token3;

    @Before
    public void setUp() throws Exception {
        nTC = new NotInTableRVComputer();
        m = new CoreMap();
        token1 = new Token("First");
        token2 = new Token("token");
        token3 = new Token("only");
        m.set(CoreAnnotations.IdAnnotation.class, "testPara");
        m.set(CoreAnnotations.IsInTableAnnotation.class, true);

    }

    @Test
    public void testGetValue() throws Exception {
        int value = nTC.getValue(m);
        System.out.println(value);
        assert (value == 0);

        m.set(CoreAnnotations.IsInTableAnnotation.class, false);
        value = nTC.getValue(m);
        assert (value == 1);
    }

    @Test
    public void testGetNumVals() throws Exception {
        int value = nTC.getNumVals();
        assert (value == 2);
    }
}