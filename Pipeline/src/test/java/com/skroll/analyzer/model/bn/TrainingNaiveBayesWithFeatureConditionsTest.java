package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrainingNaiveBayesWithFeatureConditionsTest extends TestCase {

    public void testAddSample() throws Exception {
//        NaiveBayesWithFeatureConditions nb =
//                NBTrainingHelper.createTrainingNBWithFeatureConditioning(
//                        new NBFCConfig(
//                                RandomVariableType.PARAGRAPH_HAS_DEFINITION,
//                                    new ArrayList<RandomVariableType>(),
//                                    Arrays.asList(RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE),
//                                    Arrays.asList(RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES),
//                                    DocumentAnnotatingModel.DEFAULT_WORDS));
        NaiveBayesWithFeatureConditions nb =
                NBTrainingHelper.createTrainingNBWithFeatureConditioning(
                        new NBFCConfig(
                                new RandomVariable(2, "paraIsDef"),
                                new ArrayList<RandomVariable>(),
                                Arrays.asList(new RandomVariable(2, "startsWithQuote")),
                                Arrays.asList(new RandomVariable(2, "defInQuotes")),
                                Arrays.asList(new RandomVariable(0, "words"))));


        System.out.println("initial model");
        System.out.println(nb);
        // variables are in the order of category, feature, features exist at doc level, document feature

        List<String[]> wordsList = new ArrayList<>();
        wordsList.add(new String[]{"a"});
        SimpleDataTuple tuple = new SimpleDataTuple(wordsList, new int[]{0,0,1} );

        NBTrainingHelper.addSample(nb, tuple);
        System.out.println("model after");
        System.out.println(nb);
        assert(nb.getFeatureExistAtDocLevelNodes().get(0).getParameters()[4]==1.1);

    }
}