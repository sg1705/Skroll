package com.skroll.analyzer.model.applicationModel.randomVariables;

import com.google.common.collect.Lists;
import com.skroll.document.CoreMap;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirstWordsComputerTest {

    public static final Logger logger = LoggerFactory.getLogger(FirstWordsComputerTest.class);

    CoreMap m = new CoreMap();
    Token token1 = new Token("First");
    Token token2 = new Token("token");
    Token token3 = new Token("only");

    @Test
    public void testGetWords() throws Exception {
        FirstWordsComputer fWC = new FirstWordsComputer();
        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token1, token2, token3));
        String[] words = fWC.getWords(m);
        logger.info(words[0]);
        assert (words[0].equals("First".toLowerCase()));
        assert (words.length == 1);
    }

    @Test
    public void testGetWords1() throws Exception {
        FirstWordsComputer fWC = new FirstWordsComputer();
        m.set(CoreAnnotations.TokenAnnotation.class, Lists.newArrayList(token1, token2, token3));
        String[] words = fWC.getWords(m, 3);
        assert (words[0].toLowerCase().equals("First".toLowerCase()));
        assert (words.length == 1);

    }
}