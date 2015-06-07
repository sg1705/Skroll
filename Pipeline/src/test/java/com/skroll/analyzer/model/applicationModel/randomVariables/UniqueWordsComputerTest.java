package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.google.common.collect.Lists;
import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class UniqueWordsComputerTest {

    UniqueWordsComputer nTC;
    CoreMap m = new CoreMap();
    Token token1;
    Token token2;
    Token token3;
    Token token4;

    @Before
    public void setUp() throws Exception {
        nTC = new UniqueWordsComputer();
        m = new CoreMap();
        token1 = new Token("First");
        token2 = new Token("token");
        token3 = new Token("only");
        token4 = new Token("only");
        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token1, token2, token3, token4));
        m.set(CoreAnnotations.IsInTableAnnotation.class, true);

    }

    @Test
    public void testGetWords() throws Exception {
        String[] words = nTC.getWords(m);
        assert (words.length == 3);
    }

    @Test
    public void testGetWords1() throws Exception {
        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token1, token2));
        String[] words = nTC.getWords(m, 2);
        assert (words.length == 2);
    }
}