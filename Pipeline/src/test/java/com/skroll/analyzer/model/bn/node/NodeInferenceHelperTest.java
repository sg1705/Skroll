package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.RandomVariableType;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class NodeInferenceHelperTest {


    private DiscreteNode tNode;
    private DiscreteNode parentNode;

    private RandomVariable[] parentVariables = new RandomVariable[1];
    private RandomVariable nodeVar = new RandomVariable(2, "bold");
    private List<RandomVariable> familyVariables = new ArrayList<>();

    private WordNode wNode;
    private String observedString = "abc";

    @Before
    public void setup() {
        this.parentNode = new DiscreteNode(new DiscreteNode[0]);
        parentVariables[0] = new RandomVariable(2, "defIsBold");
        parentNode.setFamilyVariables(parentVariables);
//        parentNode.setParameters(parameters);
        familyVariables.add(nodeVar);
        familyVariables.addAll(Arrays.asList(parentVariables));

        wNode = NodeTrainingHelper.createTrainingWordNode(parentNode);
        wNode.getParent().setObservation(1);
        wNode.setObservation(new String[]{observedString});
        tNode = NodeTrainingHelper
                .createTrainingDiscreteNode(this.familyVariables,
                        Arrays.asList(parentNode));
        tNode.setParameters(new double[]{1, 2, 3, 4});

    }


    @Test
    public void testCreateLogProbabilityDiscreteNode() throws Exception {
        DiscreteNode pNode = NodeInferenceHelper.createLogProbabilityDiscreteNode(tNode, Arrays.asList(parentNode));
        System.out.println(pNode);
        assert (pNode.toString().equals("DiscreteNode{familyVariables=[RandomVariable{name='bold', featureSize=2, valueNames=null}, RandomVariable{name='defIsBold', featureSize=2, valueNames=null}], parameters=[-1.0986122886681098, -0.40546510810816444, -0.8472978603872037, -0.5596157879354228]}"));



    }

    @Test
    public void testCreateLogProbabilityDiscreteNode1() throws Exception {

    }

    @Test
    public void testGetLogBelief() throws Exception {

    }

    @Test
    public void testSumOutOtherNodesWithObservationAndBelief() throws Exception {

    }

    @Test
    public void testSumOutOtherNodesWithObservationAndMessage() throws Exception {

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
}