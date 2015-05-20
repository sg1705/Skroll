package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.DocumentAnnotatingModel;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.inference.BNInference;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

public class TrainingDiscreteNodeTest {
    DiscreteNode node;
    @Before
    public void initialize(){
        node = NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(new RandomVariable(2, "numberTokens")));
    }

    @Test
    public void testUpdateCount() throws Exception {

        initialize();
        System.out.println(node);
        node.setObservation(3);
        NodeTrainingHelper.updateCount(node);
        System.out.println("after update count");
        System.out.println(node);
        NodeTrainingHelper.updateCount(node, -0.2);
        System.out.println("after update count by -0.2");
        System.out.println(node);

    }


    @Test
    public void testGetProbabilities() throws Exception {

        initialize();
        testUpdateCount();
        System.out.println("log probabilities:");
        System.out.println(Arrays.toString(NodeTrainingHelper.getLogProbabilities(node)));

        System.out.println("probabilities:");
        double[] prob = NodeTrainingHelper.getLogProbabilities(node).clone();
        BNInference.convertLogBeliefToProb(prob);
        System.out.println(Arrays.toString(prob));

    }

    @Test
    public void testToString() throws Exception {
        System.out.println(node);
    }
}