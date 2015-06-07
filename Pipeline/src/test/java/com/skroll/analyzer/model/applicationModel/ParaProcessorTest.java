package com.skroll.analyzer.model.applicationModel;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.applicationModel.ParaProcessor;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import com.skroll.document.Token;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei on 5/10/15.
 */
public class ParaProcessorTest {
    CoreMap para = new CoreMap();
    List<String> strings = Arrays.asList("\"", "in", "\"", "out", "out");
    List<Token> tokens = DocumentHelper.createTokens(strings);

    @Before
    public void setUp() throws Exception {
        tokens.get(1).set(CoreAnnotations.IsBoldAnnotation.class, true);
        para.set(CoreAnnotations.IsBoldAnnotation.class, true);
        para.set(CoreAnnotations.IsItalicAnnotation.class, true);
        para.set(CoreAnnotations.TokenAnnotation.class, tokens);
    }

    @Test
    public void testProcessParagraph() throws Exception {
        CoreMap processedPara = ParaProcessor.processParagraph(para);
        List<Token> processedTokens = processedPara.get(CoreAnnotations.TokenAnnotation.class);
        assert (processedTokens.size() == 3);
        assert (processedTokens.get(0).get(CoreAnnotations.InQuotesAnnotation.class).equals(Boolean.TRUE));

    }

    @Test
    public void testGetFeatureValue() throws Exception {
        int result = ParaProcessor.getFeatureValue(RVCreater.createRVFromAnnotation(CoreAnnotations.IsBoldAnnotation.class), Lists.newArrayList(para));
        assert (result == 1);

        result = ParaProcessor.getFeatureValue(RVCreater.createRVFromAnnotation(CoreAnnotations.IsItalicAnnotation.class), Lists.newArrayList(para));
        assert (result == 1);
    }

    @Test
    public void testGetWordFeatureValue() throws Exception {
        int result = ParaProcessor.getWordFeatureValue(
                RVCreater.createRVFromAnnotation(CoreAnnotations.IsBoldAnnotation.class),
                tokens.get(1),
                Lists.newArrayList(para));

        assert (result == 1);

        result = ParaProcessor.getWordFeatureValue(
                RVCreater.createRVFromAnnotation(CoreAnnotations.IsBoldAnnotation.class),
                tokens.get(0),
                Lists.newArrayList(para));
        assert (result == 0);

    }

    @Test
    public void testGetFeatureVals() throws Exception {
        CoreMap para2 = new CoreMap();
        List<String> strings2 = Arrays.asList("First", "token", "on", "this");
        List<Token> tokens2 = DocumentHelper.createTokens(strings);
        tokens2.get(1).set(CoreAnnotations.IsBoldAnnotation.class, true);
        para2.set(CoreAnnotations.IsBoldAnnotation.class, true);
        para2.set(CoreAnnotations.IsUnderlineAnnotation.class, false);
        para2.set(CoreAnnotations.TokenAnnotation.class, tokens);

        int[] values = ParaProcessor.getFeatureVals(Lists.newArrayList(
                RVCreater.createRVFromAnnotation(CoreAnnotations.IsBoldAnnotation.class),
                RVCreater.createRVFromAnnotation(CoreAnnotations.IsItalicAnnotation.class)),
                Lists.newArrayList(para, para2));

        System.out.println(Arrays.toString(values));
        assert(values[0] == 1 );
        assert(values[1] == 1 );

        values = ParaProcessor.getFeatureVals(Lists.newArrayList(
                        RVCreater.createRVFromAnnotation(CoreAnnotations.IsUnderlineAnnotation.class),
                        RVCreater.createRVFromAnnotation(CoreAnnotations.IsItalicAnnotation.class)),
                Lists.newArrayList(para, para2));
        System.out.println(Arrays.toString(values));

        assert(values[0] == 0 );
        assert(values[1] == 1 );


    }


    @Test
    public void testIsParaObserved() throws Exception {
        para.set(CoreAnnotations.IsUserObservationAnnotation.class, true);
        boolean isObserved = ParaProcessor.isParaObserved(para);
        assert(isObserved);

        para.set(CoreAnnotations.IsUserObservationAnnotation.class, false);
        isObserved = ParaProcessor.isParaObserved(para);
        assert(!isObserved);

    }

}