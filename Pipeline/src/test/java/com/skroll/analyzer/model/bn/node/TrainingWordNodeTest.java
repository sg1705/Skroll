package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariableType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TrainingWordNodeTest {
    TrainingDiscreteNode parent = new TrainingDiscreteNode(Arrays.asList(RandomVariableType.PARAGRAPH_HAS_DEFINITION));
    TrainingWordNode node = new TrainingWordNode(parent);

    @BeforeClass
    public static void setUpOnce() throws  Exception{

    }

    //todo: check prior parameters...
    @Test
    public void testUpdateCount() throws Exception {

        System.out.println("before update\n");
        System.out.println(node);
        parent.setObservation(1);
        node.setObservation(new String[]{"means"});
        node.updateCount();
        System.out.println("after update\n");
        System.out.println(node);

    }

    @Test
    public void testUpdateCount1() throws Exception {

    }

    @Test
    public void testUpdateCount2() throws Exception {

    }

    @Test
    public void testUpdateCount3() throws Exception {

    }

    @Test
    public void testGetProbabilities() throws Exception {

    }
}