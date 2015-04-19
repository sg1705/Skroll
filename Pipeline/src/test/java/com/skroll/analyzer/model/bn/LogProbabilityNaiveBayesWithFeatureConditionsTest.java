package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.DocumentAnnotatingModel;
import com.skroll.analyzer.model.RandomVariableType;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogProbabilityNaiveBayesWithFeatureConditionsTest extends TestCase {

    public void testCreatFromTraining(){
        TrainingNaiveBayesWithFeatureConditions nb = new TrainingNaiveBayesWithFeatureConditions(
                RandomVariableType.PARAGRAPH_HAS_DEFINITION,
                new ArrayList<RandomVariableType>(),
                Arrays.asList(RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE),
                Arrays.asList(RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES),
                DocumentAnnotatingModel.DEFAULT_WORDS);

        System.out.println("initial model");
        System.out.println(nb);
        // variables are in the order of category, feature, features exist at doc level, document feature
        List<String[]> wordsList = new ArrayList<>();
        wordsList.add(new String[]{"a"});
        SimpleDataTuple tuple = new SimpleDataTuple(wordsList, new int[]{0,0,1} );
        nb.addSample(tuple);
        System.out.println("model after");
        System.out.println(nb);

        LogProbabilityNaiveBayesWithFeatureConditions pbn = new LogProbabilityNaiveBayesWithFeatureConditions(nb);
        System.out.println("probability model");
        System.out.println(pbn);

        assert((int)(100*pbn.getFeatureExistAtDocLevelArray()[0].getParameters()[4])==-8);
    }

}