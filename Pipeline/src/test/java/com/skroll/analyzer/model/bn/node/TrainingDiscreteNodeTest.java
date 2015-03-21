package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.DocumentAnnotatingModel;
import com.skroll.analyzer.model.RandomVariableType;
import junit.framework.TestCase;

import java.util.Arrays;

public class TrainingDiscreteNodeTest extends TestCase {
    TrainingDiscreteNode node;
    void initialize(){
        node = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_NUMBER_TOKENS));
        node.setParents(new TrainingDiscreteNode[0]);
    }

    public void testUpdateCount() throws Exception {

        initialize();
        System.out.println(node);
        node.setObservation(3);
        node.updateCount();
        System.out.println("after update count");
        System.out.println(node);
        node.updateCount(-0.2);
        System.out.println("after update count by -0.2");
        System.out.println(node);

    }


    public void testGetProbabilities() throws Exception {

        initialize();
        testUpdateCount();
        System.out.println("probabilities:");
        System.out.println(Arrays.toString(node.getProbabilities()));

    }

    public void testToString() throws Exception {
        TrainingDiscreteNode node = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_NUMBER_TOKENS));
        System.out.println(node);
    }
}