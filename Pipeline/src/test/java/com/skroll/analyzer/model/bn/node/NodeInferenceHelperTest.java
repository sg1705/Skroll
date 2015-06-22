package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeInferenceHelperTest {

    RandomVariable rv = new RandomVariable(2, "bold");
    RandomVariable prv = new RandomVariable(2, "paraIsDef");
    RandomVariable prv2 = new RandomVariable(2, "defsAreBold");

    private DiscreteNode node;
    private DiscreteNode parentNode = NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(prv));
    private DiscreteNode parentNode2 = NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(prv2));
    private List<DiscreteNode> parentNodes = Arrays.asList(parentNode, parentNode2);

    private RandomVariable[] parentVariables = new RandomVariable[]{prv, prv2};
    private RandomVariable nodeVar = new RandomVariable(2, "bold");
    private List<RandomVariable> familyVariables = Arrays.asList(rv, prv, prv2);

    private WordNode wNode;
    private String observedString = "abc";


    private List<List<RandomVariable>> nbmnDocFeature = RVCreater.createNBMNDocFeatureRVs(Arrays.asList(rv), prv, prv2.getName());
    private List<DiscreteNode> nbmnParentNodes = Arrays.asList(
            NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(nbmnDocFeature.get(0).get(0))),
            NodeTrainingHelper.createTrainingDiscreteNode(Arrays.asList(nbmnDocFeature.get(0).get(1))));
    private List<RandomVariable> nbmnFamilyVariables = new ArrayList(Arrays.asList(rv, prv));
    MultiplexNode tMultiNode;


    @Before
    public void setup() {
        node = NodeTrainingHelper
                .createTrainingDiscreteNode(familyVariables,
                        Arrays.asList(parentNode, parentNode2));

        wNode = NodeTrainingHelper.createTrainingWordNode(parentNode);
        wNode.getParent().setObservation(1);
        wNode.setObservation(new String[]{observedString});

        node.setParameters(new double[]{1, 2, 3, 4, 5, 6, 7, 8});
        parentNode.setParameters(new double[]{1, 2});
        nbmnFamilyVariables.addAll(nbmnDocFeature.get(0));

        tMultiNode = NodeTrainingHelper
                .createTrainingMultiplexNode(this.nbmnFamilyVariables, parentNode, this.nbmnParentNodes);

        DiscreteNode[] nodes = tMultiNode.getNodes();
        nodes[0].setParameters(new double[]{1, 2, 3, 4});
        nodes[1].setParameters(new double[]{5, 6, 7, 8});
    }


    @Test
    public void testCreateLogProbabilityDiscreteNode() throws Exception {
        DiscreteNode pNode = NodeInferenceHelper.createLogProbabilityDiscreteNode(node, Arrays.asList(parentNode, parentNode2));
        System.out.println(pNode);
        assert (pNode.toString().equals("DiscreteNode{familyVariables=[RandomVariable{name='bold', featureSize=2, valueNames=null}, RandomVariable{name='paraIsDef', featureSize=2, valueNames=null}, RandomVariable{name='defsAreBold', featureSize=2, valueNames=null}], parameters=[-1.0986122886681098, -0.40546510810816444, -0.8472978603872037, -0.5596157879354228, -0.7884573603642702, -0.6061358035703156, -0.7621400520468967, -0.6286086594223742]}"));
    }

    @Test
    public void testCreateLogProbabilityDiscreteNodeWithNoParents() throws Exception {
        DiscreteNode pParentNode = NodeInferenceHelper.createLogProbabilityDiscreteNode(parentNode);
        System.out.println(pParentNode);
        assert (pParentNode.toString().equals("DiscreteNode{familyVariables=[RandomVariable{name='paraIsDef', featureSize=2, valueNames=null}], parameters=[-1.0986122886681098, -0.40546510810816444]}"));
    }

    @Test
    public void testGetLogBelief() throws Exception {
//        DiscreteNode pNode = NodeInferenceHelper.createLogProbabilityDiscreteNode(node, Arrays.asList(parentNode, parentNode2));
//        System.out.println("original parameters = " + Arrays.toString(pNode.getParameters()));
//        double [] result = NodeInferenceHelper.getLogBelief(pNode, 1, new double[]{-1,-2});
//        System.out.println("result = " + Arrays.toString(result));
//
//        assert(Arrays.toString(result).equals("[-2.09861228866811, -1.4054651081081644, -2.8472978603872034, -2.5596157879354227, -1.7884573603642702, -1.6061358035703157, -2.7621400520468966, -2.6286086594223743]"));

        node.setParameters(new double[]{0.0, 0, 0, 0, 0, 0, 0, 0});
        double[] result = NodeInferenceHelper.getLogBelief(node, 1, new double[]{-1, -2});
        System.out.println("result = " + Arrays.toString(result));
        assert (Arrays.toString(result).equals("[-1.0, -1.0, -2.0, -2.0, -1.0, -1.0, -2.0, -2.0]"));
    }

    @Test
    public void testSumOutOtherNodesWithObservationAndBelief() throws Exception {

    }

    @Test
    public void testSumOutOtherNodesWithObservationAndMessage() throws Exception {
        node.setObservation(1);
        double[] newMessage = NodeInferenceHelper.sumOutOtherNodesWithObservationAndMessage(
                node, parentNode, new double[]{1, -1}, parentNode2);
        System.out.println(Arrays.toString(newMessage));
        assert (Arrays.toString(newMessage).equals("[3.6931471805599454, 7.693147180559945]"));

    }

    @Test
    public void testSumOutOtherNodesWithObservationAndMessage1() throws Exception {

    }

    @Test
    public void testSumOutOtherNodesWithObservation() throws Exception {

    }

    @Test
    public void testSumOutOtherNodesWithObservation1() throws Exception {

    }

    @Test
    public void testCreateLogProbabilityWordNode() throws Exception {

    }

    @Test
    public void testSumOutWordsWithObservation() throws Exception {

    }

    @Test
    public void testCreateLogProbabilityMultiplexNode() throws Exception {

        MultiplexNode pMultiNode = NodeInferenceHelper.createLogProbabilityMultiplexNode(tMultiNode, parentNode, nbmnParentNodes);
        System.out.println(pMultiNode);
    }

    @Test
    public void testUpdateMessageToSelectingNode() throws Exception {
//        MultiplexNode pMultiNode = NodeInferenceHelper.createLogProbabilityMultiplexNode(tMultiNode,parentNode,  Arrays.asList(parentNode2));
        MultiplexNode pMultiNode = NodeInferenceHelper.createLogProbabilityMultiplexNode(tMultiNode, parentNode, nbmnParentNodes);
        pMultiNode.setObservation(1);
        pMultiNode.getNodes()[0].setParameters(new double[]{0, 1, 2, 3});
        pMultiNode.getNodes()[1].setParameters(new double[]{4, 5, 6, 7});
        double[] message = NodeInferenceHelper.updateMessageToSelectingNode(pMultiNode, new double[][]{{1, -1}, {0, 2}});
        System.out.println(Arrays.toString(message));
        assert (Arrays.toString(message).equals("[2.6931471805599454, 9.01814992791781]"));
    }

    @Test
    public void testUpdateMessagesFromSelectingNode() throws Exception {
        MultiplexNode pMultiNode = NodeInferenceHelper.createLogProbabilityMultiplexNode(tMultiNode, parentNode, nbmnParentNodes);
        pMultiNode.setObservation(1);
        pMultiNode.getNodes()[0].setParameters(new double[]{0, 1, 2, 3});
        pMultiNode.getNodes()[1].setParameters(new double[]{4, 5, 6, 7});
        double[][] message = NodeInferenceHelper.updateMessagesFromSelectingNode(pMultiNode, new double[]{1, -1});
        System.out.println(Arrays.deepToString(message));
        assert (Arrays.deepToString(message).equals("[[2.0, 4.0], [4.0, 6.0]]"));

    }
}