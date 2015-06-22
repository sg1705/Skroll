package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.analyzer.model.bn.NBMNTuple;
import com.skroll.analyzer.model.bn.NBTrainingHelper;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeTrainingHelperTest {

    private RandomVariable catVar = new RandomVariable(2, "paraIsDef");
    private RandomVariable parentVar = new RandomVariable(2, "defInBold");
    private DiscreteNode catNode = NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(catVar));
    private DiscreteNode parentNode = NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(parentVar));
    private RandomVariable var = new RandomVariable(2, "paraIsBold");

    private List<List<RandomVariable>> nbmnDocFeature = RVCreater.createNBMNDocFeatureRVs(Arrays.asList(var), catVar, parentVar.getName());
    private List<DiscreteNode> nbmnParentNodes = Arrays.asList(
            NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(nbmnDocFeature.get(0).get(0))),
            NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(nbmnDocFeature.get(0).get(1))));

    private List<DiscreteNode> parentNodes = Arrays.asList(catNode, parentNode);
    private RandomVariable[] parentVariables = new RandomVariable[]{catVar, parentVar};

    //    private List<RandomVariable> nodeVariables = Arrays.asList(var, catVar, parentVar);
    private List<RandomVariable> familyVariables = Arrays.asList(var, catVar, parentVar);
    private List<RandomVariable> nbmnFamilyVariables = new ArrayList(Arrays.asList(var, catVar));

    private double[] parameters = {NodeTrainingHelper.PRIOR_COUNT, NodeTrainingHelper.PRIOR_COUNT,
                NodeTrainingHelper.PRIOR_COUNT, NodeTrainingHelper.PRIOR_COUNT,};

    private WordNode wNode;
    private String observedString = "abc";


    @Before
    public void setup() {
        parentNode.setParameters(parameters);

        wNode = NodeTrainingHelper.createTrainingWordNode(parentNode);
        wNode.getParent().setObservation(1);
        wNode.setObservation(new String[]{observedString});
        nbmnFamilyVariables.addAll(nbmnDocFeature.get(0));
    }


    @Test
    public void testCreateTrainingDiscreteNode() throws Exception {
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.familyVariables,
                        parentNodes);


        assert (node.getFamilyVariables().length == familyVariables.size());

        assert (node.getParameters().length == 8);
        assert (node.getParameter(0) == NodeTrainingHelper.PRIOR_COUNT);
        assert (node.getParameter(1) == NodeTrainingHelper.PRIOR_COUNT);
    }

    @Test
    public void testCreateTrainingDiscreteNodeWithNoParents() throws Exception {
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(Arrays.asList(new RandomVariable(2, "testVar")));


        assert (node.getFamilyVariables().length == 1);

        assert( node.getParameters().length == 2);
        assert (node.getParameter(0) == NodeTrainingHelper.PRIOR_COUNT);
        assert (node.getParameter(1) == NodeTrainingHelper.PRIOR_COUNT);

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
                        parentNodes);

        node.parents[0].setObservation(1);
        node.parents[1].setObservation(0);
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
                        parentNodes);

        node.parents[0].setObservation(1);
        node.parents[1].setObservation(0);
        node.setObservation(1);

        NodeTrainingHelper.updateCount(node);
        assert (node.getParameter(3) == 1 + NodeTrainingHelper.PRIOR_COUNT);

    }

    @Test
    public void testGetLogProbabilities() throws Exception {
        String result = "[-0.6931471805599453, -0.6931471805599453, -2.4849066497880004, -0.08701137698962981, -0.6931471805599453, -0.6931471805599453, -0.6931471805599453, -0.6931471805599453]";
        DiscreteNode node = NodeTrainingHelper
                .createTrainingDiscreteNode(this.familyVariables,
                        parentNodes);

        node.parents[0].setObservation(1);
        node.parents[1].setObservation(0);
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

    @Test
    public void testCreateTrainingMultiplexNode() throws Exception {
        MultiplexNode node = NodeTrainingHelper
                .createTrainingMultiplexNode(this.nbmnFamilyVariables, catNode,
                        nbmnParentNodes);

        System.out.println(Arrays.toString(node.getNodes()[0].getParameters()));
        System.out.println(Arrays.toString(node.getNodes()[1].getParameters()));
        assert (node.getNodes().length == 2);
        assert (Arrays.toString(node.getNodes()[0].getParameters()).equals("[0.1, 0.1, 0.1, 0.1]"));
        assert (Arrays.toString(node.getNodes()[1].getParameters()).equals("[0.1, 0.1, 0.1, 0.1]"));
    }

    @Test
    public void testUpdateMultiNodeCount() throws Exception {

        MultiplexNode node = NodeTrainingHelper
                .createTrainingMultiplexNode(this.nbmnFamilyVariables, catNode, nbmnParentNodes);

        nbmnParentNodes.get(0).setObservation(0);
        nbmnParentNodes.get(1).setObservation(1);
        catNode.setObservation(0);
        node.setObservation(1);
        NodeTrainingHelper.updateCount(node, 12.0);
        System.out.println(Arrays.toString(node.getNodes()[0].getParameters()));
        System.out.println(Arrays.toString(node.getNodes()[1].getParameters()));
        assert (Arrays.toString(node.getNodes()[0].getParameters()).equals("[0.1, 12.1, 0.1, 0.1]"));
        assert (Arrays.toString(node.getNodes()[1].getParameters()).equals("[0.1, 0.1, 0.1, 0.1]"));

        catNode.setObservation(1);
        NodeTrainingHelper.updateCount(node, 12.0);
        assert (Arrays.toString(node.getNodes()[0].getParameters()).equals("[0.1, 12.1, 0.1, 0.1]"));
        assert (Arrays.toString(node.getNodes()[1].getParameters()).equals("[0.1, 0.1, 0.1, 12.1]"));


//        assert (node.getNodes()[0].getParameter(3) == 12 + NodeTrainingHelper.PRIOR_COUNT);
        NodeTrainingHelper.updateCount(node, -12.0);
        System.out.println("after update count by -12");
        System.out.println(node);

//        NBMNTuple tuple = new NBMNTuple(null,  0, null,new int[]{1}, new int[]{0});

    }

    @Test
    public void testUpdateMultiNodeCountWithDefaultWeight() throws Exception {


        MultiplexNode node = NodeTrainingHelper
                .createTrainingMultiplexNode(this.nbmnFamilyVariables, catNode, nbmnParentNodes);

        nbmnParentNodes.get(0).setObservation(0);
        nbmnParentNodes.get(1).setObservation(1);
        catNode.setObservation(0);
        node.setObservation(1);
        NodeTrainingHelper.updateCount(node, 1.0);
        System.out.println(Arrays.toString(node.getNodes()[0].getParameters()));
        System.out.println(Arrays.toString(node.getNodes()[1].getParameters()));
        assert (Arrays.toString(node.getNodes()[0].getParameters()).equals("[0.1, 1.1, 0.1, 0.1]"));
        assert (Arrays.toString(node.getNodes()[1].getParameters()).equals("[0.1, 0.1, 0.1, 0.1]"));
//        assert (node.getNodes()[0].getParameter(3) == 1 + NodeTrainingHelper.PRIOR_COUNT);

    }

//    @Test
//    public void testGetLogProbabilitiesMultiNode() throws Exception {
//        String result = "[[-0.6931471805599453, -0.6931471805599453, -2.4849066497880004, -0.08701137698962981]]";
//        MultiplexNode node = NodeTrainingHelper
//                .createTrainingMultiplexNode(this.familyVariables,
//                        parentNodes);
//
//        parentNode.setObservation(1);
//        catNode.setObservation(0);
//        node.setObservation(1);
//        NodeTrainingHelper.updateCount(node);
//        System.out.println(Arrays.deepToString(NodeTrainingHelper.getLogProbabilities(node)));
//        assert (Arrays.deepToString(NodeTrainingHelper.getLogProbabilities(node)).equals(result));
//
//    }
}