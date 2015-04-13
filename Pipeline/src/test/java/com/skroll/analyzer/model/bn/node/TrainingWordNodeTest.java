package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariableType;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

public class TrainingWordNodeTest {
    DiscreteNode parent = NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(RandomVariableType.PARAGRAPH_HAS_DEFINITION));
    WordNode node = new WordNode(parent);

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
        NodeTrainingHelper.updateCount(node);
        System.out.println("after update\n");
        System.out.println(node);

    }

}