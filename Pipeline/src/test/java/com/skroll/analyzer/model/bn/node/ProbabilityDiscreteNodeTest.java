package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariableType;
import junit.framework.TestCase;

import java.util.Arrays;

public class ProbabilityDiscreteNodeTest extends TestCase {

    ProbabilityDiscreteNode fNode, pNode, dNode;
    void initialize(){
        TrainingDiscreteNode tfNode = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE));
        TrainingDiscreteNode tdNode = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES));
        TrainingDiscreteNode tpNode = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_HAS_DEFINITION));
        tfNode.setParents( Arrays.asList(tpNode, tdNode).toArray(new DiscreteNode[2]));

        ProbabilityDiscreteNode fNode = new ProbabilityDiscreteNode(tfNode);
        ProbabilityDiscreteNode pNode = new ProbabilityDiscreteNode(tpNode);
        ProbabilityDiscreteNode dNode = new ProbabilityDiscreteNode(tdNode);
        fNode.setParents( Arrays.asList(tpNode, tdNode).toArray(new DiscreteNode[2]));


    }
    public void testCreateProbabilityNodeFromTrainingNode(){
        TrainingDiscreteNode node;
        node = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_NUMBER_TOKENS));
        node.setParents(new TrainingDiscreteNode[0]);

        ProbabilityDiscreteNode pNode = new ProbabilityDiscreteNode(node);
    }

    public void testGetProbabilities() throws Exception {

        System.out.println("test");

    }

    public void testSumOutOtherNodesWithObservation() throws Exception {

    }
}