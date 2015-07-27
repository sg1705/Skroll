//package com.skroll.analyzer.model.applicationModel;
//
//import com.skroll.analyzer.model.RandomVariable;
//import com.skroll.analyzer.model.applicationModel.randomVariables.LowerCaseWordsComputer;
//import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
//import com.skroll.analyzer.model.applicationModel.randomVariables.RVValues;
////import com.skroll.analyzer.model.applicationModel.randomVariables.UniqueWordsComputer;
//import com.skroll.document.CoreMap;
//import com.skroll.document.Document;
//import org.junit.Test;
//
//import java.util.Arrays;
//
///**
// * Created by wei on 5/21/15.
// */
//public class LowerCaseWordsComputerTest {
//
//    @Test
//    public void testGetWords() throws Exception {
//        RandomVariable rv = RVCreater.createWordsRVWithComputer(new LowerCaseWordsComputer(), "lowerCaseWords");
//        Document doc = TestHelper.setUpTestDoc();
//        CoreMap para = doc.getParagraphs().get(0);
//        System.out.println(Arrays.toString(RVValues.getWords(rv, para)));
//
//    }
//
//}