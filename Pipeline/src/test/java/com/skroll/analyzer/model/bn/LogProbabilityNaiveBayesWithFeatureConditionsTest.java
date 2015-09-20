package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.bn.config.NBFCConfig;
import com.skroll.analyzer.model.bn.inference.BNInference;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class LogProbabilityNaiveBayesWithFeatureConditionsTest extends TestCase {

    public void testCreatFromTraining(){
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

        NaiveBayesWithFeatureConditions pbn = NBInferenceHelper.createLogProbNBWithFeatureConditions(nb);

        System.out.println("log probability model");
        System.out.println(pbn);

        double[] para = pbn.getFeatureExistAtDocLevelNodes().get(0).getParameters();
        double[] prob = para.clone();
        BNInference.exp(prob);
        System.out.println("probabilities"+ Arrays.toString(prob));
        //assert((int)(100*pbn.getFeatureExistAtDocLevelNodes().get(0).getParameters()[4])==66);
        assert ((int) (10000 * prob[4]) == 9999);
    }

}