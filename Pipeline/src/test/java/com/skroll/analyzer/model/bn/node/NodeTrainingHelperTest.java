package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeTrainingHelperTest {

    private DiscreteNode parentNode;
    private RandomVariable[] parentVariables = new RandomVariable[1];
    private List<RandomVariable> nodeVariables = new ArrayList();
    private List<RandomVariable> familyVariables;
    private double[] parameters = {NodeTrainingHelper.PRIOR_COUNT, NodeTrainingHelper.PRIOR_COUNT,
                NodeTrainingHelper.PRIOR_COUNT, NodeTrainingHelper.PRIOR_COUNT,};

    private WordNode wNode;
    private String observedString = "abc";

    @Before
    public void setup() {
        this.parentNode = new DiscreteNode(new DiscreteNode[0]);
        parentVariables[0] = new RandomVariable(2, "defIsBold");
        nodeVariables.add(parentVariables[0]);
        parentNode.setFamilyVariables(parentVariables);
        parentNode.setParameters(parameters);
        familyVariables = new ArrayList();
        familyVariables.addAll(nodeVariables);
        familyVariables.addAll(Arrays.asList(parentVariables));

        wNode = NodeTrainingHelper.createTrainingWordNode(parentNode);
        wNode.getParent().setObservation(1);
        wNode.setObservation(new String[]{observedString});
    }


    @Test
    public void testCreateTrainingDiscreteNode() throws Exception {
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.familyVariables,
                        Arrays.asList(parentNode));


        assert (node.getFamilyVariables().length == familyVariables.size());

        assert( node.getParameters().length == parameters.length);
        assert( node.getParameter(0) == parameters[0]);
        assert( node.getParameter(1) == parameters[1]);
    }

    @Test
    public void testCreateTrainingDiscreteNodeWithNoParents() throws Exception {
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.nodeVariables);


        assert (node.getFamilyVariables().length == nodeVariables.size());

        assert( node.getParameters().length == 2);
        assert( node.getParameter(0) == parameters[0]);
        assert( node.getParameter(1) == parameters[1]);

    }

    @Test
    public void testGetIndex() throws Exception {

        int index = NodeTrainingHelper.getIndex(familyVariables.toArray(new RandomVariable[familyVariables.size()]),
                new int[] {0,1});

        assert (index  == 2);
    }

    @Test
    public void testUpdateCount() throws Exception {
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.familyVariables,
                        Arrays.asList(parentNode));

        node.parents[0].setObservation(1);
        node.setObservation(1);
        NodeTrainingHelper.updateCount(node, 12.0);
        assert (node.getParameter(3) == 12 + NodeTrainingHelper.PRIOR_COUNT);
        NodeTrainingHelper.updateCount(node, -12.0);
        System.out.println("after update count by -12");
        System.out.println(node);


    }

    @Test
    public void testUpdateCountWithDefaultWeight() throws Exception {
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.familyVariables,
                        Arrays.asList(parentNode));

        node.parents[0].setObservation(1);
        node.setObservation(1);
        NodeTrainingHelper.updateCount(node);
        assert (node.getParameter(3) == 1 + NodeTrainingHelper.PRIOR_COUNT);

    }

    @Test
    public void testGetLogProbabilities() throws Exception {
        String result = "[-0.6931471805599453, -0.6931471805599453, -2.4849066497880004, -0.08701137698962981]";
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.familyVariables,
                        Arrays.asList(parentNode));

        node.parents[0].setObservation(1);
        node.setObservation(1);
        NodeTrainingHelper.updateCount(node);
        System.out.println(Arrays.toString(NodeTrainingHelper.getLogProbabilities(node)));
        assert (Arrays.toString(NodeTrainingHelper.getLogProbabilities(node)).equals(result));
    }

    @Test
    public void testCreateTrainingWordNode() throws Exception {
        WordNode wNode = NodeTrainingHelper.createTrainingWordNode(parentNode);
        assert (wNode.getParent() == parentNode);

    }

    @Test
    public void testUpdateWordNodeCount() throws Exception {
        NodeTrainingHelper.updateCount(wNode, 12);
        System.out.println("word node after updating with observed word abc");
        System.out.println(wNode);
        assert (wNode.getParameters().get(observedString)[1] == 12);

    }

    @Test
    public void testUpdateWordNodeDefaultCount() throws Exception {
        NodeTrainingHelper.updateCount(wNode);
        System.out.println("word node after updating with observed word abc");
        System.out.println(wNode);
        assert (wNode.getParameters().get(observedString)[1] == 1);

    }

    @Test
    public void testUpdateWordNodeCountWithWord() throws Exception {
        NodeTrainingHelper.updateCount(wNode, observedString, 12);
        System.out.println("word node after updating with observed word abc");
        System.out.println(wNode);
        assert (wNode.getParameters().get(observedString)[1] == 12);

    }


    @Test
    public void testGetLogProbabilities1() throws Exception {
        String result = "[-2.302585092994046, 4.788324729085938]";
        NodeTrainingHelper.updateCount(wNode, 12);
        System.out.println("word node after updating with observed word abc");
        System.out.println(Arrays.toString(
                NodeTrainingHelper.getLogProbabilities(wNode).get(observedString)));
        assert (result.equals(Arrays.toString(
                NodeTrainingHelper.getLogProbabilities(wNode).get(observedString))));
    }
}