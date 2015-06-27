package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by wei on 6/13/15.
 */
public class NodeHelperTest {


    protected DiscreteNode node;
    protected RandomVariable[] variables = new RandomVariable[1];
    protected double[] parameters = {0.1, 0.1};

    @Before
    public void setup() {
        this.node = new DiscreteNode(new DiscreteNode[0]);
        variables[0] = new RandomVariable(2, "docDefIsBold");
        node.setFamilyVariables(variables);
        node.setParameters(parameters);
    }


    @Test
    public void testSizeUpTo() throws Exception {
        assert (NodeHelper.sizeUpTo(node, 1) == 2);

    }


}