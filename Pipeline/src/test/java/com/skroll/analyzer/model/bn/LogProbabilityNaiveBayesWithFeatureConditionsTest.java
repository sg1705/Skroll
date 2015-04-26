package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.DocumentAnnotatingModel;
import com.skroll.analyzer.model.RandomVariableType;
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
                            RandomVariableType.PARAGRAPH_HAS_DEFINITION,
                            new ArrayList<RandomVariableType>(),
                            Arrays.asList(RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE),
                            Arrays.asList(RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES),
                            DocumentAnnotatingModel.DEFAULT_WORDS));

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
        assert((int)(100*prob[4])==91);
    }

}