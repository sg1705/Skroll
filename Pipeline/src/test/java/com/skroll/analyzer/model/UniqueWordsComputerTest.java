package com.skroll.analyzer.model;

import com.skroll.document.CoreMap;
import com.skroll.document.Document;
import com.skroll.document.annotation.CoreAnnotations;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by wei on 5/21/15.
 */
public class UniqueWordsComputerTest {

    @Test
    public void testGetWords() throws Exception {
        RandomVariable rv = RVCreater.createWordsRVWithComputer(new UniqueWordsComputer(), "uniqueWords");
        Document doc = TestHelper.setUpTestDoc();
        CoreMap para = doc.getParagraphs().get(0);
        System.out.println(Arrays.toString(RVValues.getWords(rv, para)));

    }

}