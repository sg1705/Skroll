package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariableType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeTrainingHelperTest {

    private DiscreteNode parentNode;
    private RandomVariableType[] parentVariables = new RandomVariableType[1];
    private List<RandomVariableType> nodeVariables = new ArrayList();
    private List<RandomVariableType> familyVariables;
    private double[] parameters = {NodeTrainingHelper.PRIOR_COUNT, NodeTrainingHelper.PRIOR_COUNT,
                NodeTrainingHelper.PRIOR_COUNT, NodeTrainingHelper.PRIOR_COUNT,};

    @Before
    public void setup() {
        this.parentNode = new DiscreteNode(new DiscreteNode[0]);
        parentVariables[0] = RandomVariableType.DOCUMENT_DEFINITIONS_IS_BOLD;
        nodeVariables.add(RandomVariableType.DOCUMENT_DEFINITIONS_IS_BOLD);
        parentNode.setFamilyVariables(parentVariables);
        parentNode.setParameters(parameters);
        familyVariables = new ArrayList();
        familyVariables.addAll(nodeVariables);
        familyVariables.addAll(Arrays.asList(parentVariables));
    }


    @Test
    public void testCreateTrainingDiscreteNode() throws Exception {
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.familyVariables,
                        Arrays.asList(parentNode));


        assert (node.familyVariables.length == familyVariables.size());
        assert (node.getRandomVariableSizes(familyVariables)[0] == familyVariables.get(0).getFeatureSize());
        assert (node.getRandomVariableSizes(familyVariables).length == familyVariables.size());

        assert( node.getParameters().length == parameters.length);
        assert( node.getParameter(0) == parameters[0]);
        assert( node.getParameter(1) == parameters[1]);
    }

    @Test
    public void testCreateTrainingDiscreteNodeWithNoParents() throws Exception {
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.nodeVariables);


        assert (node.familyVariables.length == nodeVariables.size());

        assert( node.getParameters().length == 2);
        assert( node.getParameter(0) == parameters[0]);
        assert( node.getParameter(1) == parameters[1]);

    }

    @Test
    public void testGetIndex() throws Exception {

    }

    @Test
    public void testUpdateCount() throws Exception {

    }

    @Test
    public void testUpdateCount1() throws Exception {

    }

    @Test
    public void testGetLogProbabilities() throws Exception {

    }

    @Test
    public void testCreateTrainingWordNode() throws Exception {

    }

    @Test
    public void testUpdateCount2() throws Exception {

    }

    @Test
    public void testUpdateCount3() throws Exception {

    }

    @Test
    public void testUpdateCount4() throws Exception {

    }

    @Test
    public void testGetLogProbabilities1() throws Exception {

    }
}