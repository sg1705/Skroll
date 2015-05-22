package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by wei on 5/10/15.
 */
public class ParaProcessorTest {
    CoreMap para = new CoreMap();
    List<String> strings = Arrays.asList("\"", "in", "\"", "out", "out");
    List<Token> tokens = DocumentHelper.createTokens(strings);

    @Before
    public void setUp() throws Exception {
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
    }

    @Test
    public void testProcessParagraph() throws Exception {
        CoreMap processedPara = ParaProcessor.processParagraph(para);

//        Set<String> wordSet = processedPara.get(CoreAnnotations.WordSetForTrainingAnnotation.class);
        List<Token> processedTokens = processedPara.get(CoreAnnotations.TokenAnnotation.class);

//        assert (wordSet.size() == 2);
        assert (processedTokens.size() == 3);
        assert (processedTokens.get(0).get(CoreAnnotations.InQuotesAnnotation.class).equals(Boolean.TRUE));

    }
}