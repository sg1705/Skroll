package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import org.junit.Before;
import org.junit.Test;

public class DiscreteNodeTest {

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
    public void testSetObservation() throws Exception {
        DiscreteNode node = new DiscreteNode(new DiscreteNode[0]);
        node.setObservation(1);
        assert (node.getObservation() == 1);
    }

    @Test
    public void testClearObservation() throws Exception {
        DiscreteNode node = new DiscreteNode(new DiscreteNode[0]);
        node.setObservation(1);
        node.clearObservation();
        assert (node.getObservation() == -1);

    }

    @Test
    public void testGetParentNodeIndex() throws Exception {
        assert(node.getParentNodeIndex(null) == -1);
    }

    @Test
    public void testSetFamilyVariables() throws Exception {
        // if no parent, then only 1 random variable
        assert (node.getFamilyVariables().length == variables.length);
    }


    @Test
    public void testGetParameter() throws Exception {
        assert( node.getParameters().length == parameters.length);
        assert( node.getParameter(0) == parameters[0]);
        assert( node.getParameter(1) == parameters[1]);
    }
}