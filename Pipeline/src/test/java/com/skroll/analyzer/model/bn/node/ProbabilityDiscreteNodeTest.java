package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariableType;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

public class ProbabilityDiscreteNodeTest {

    static ProbabilityDiscreteNode fNode, pNode, dNode;

    @BeforeClass
    public static void setUpOnce() throws  Exception{
        TrainingDiscreteNode tfNode = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
                        RandomVariableType.PARAGRAPH_HAS_DEFINITION,
                        RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES
                ));
        TrainingDiscreteNode tdNode = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES));
        TrainingDiscreteNode tpNode = new TrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_HAS_DEFINITION));
        tfNode.setParents( Arrays.asList(tpNode, tdNode).toArray(new DiscreteNode[2]));

         fNode = new ProbabilityDiscreteNode(tfNode);
         pNode = new ProbabilityDiscreteNode(tpNode);
         dNode = new ProbabilityDiscreteNode(tdNode);
        fNode.setParents( Arrays.asList(pNode, dNode).toArray(new DiscreteNode[2]));
        fNode.setParameters(new double[]{0.1,0.8,0.3,0.7,0.5,0.5,0.6,0.4});



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

    @Test
    public void testSumOutOtherNodesWithObservation() throws Exception {
        //fNode.setParents( Arrays.asList(pNode).toArray(new DiscreteNode[2]));
        //fNode.setParameters(new double[]{0.1,0.8,0.3,0.7});
        System.out.println(fNode);
        fNode.setObservation(1);
        double[] message = fNode.sumOutOtherNodesWithObservation(pNode);
        System.out.println("with observation 1, message="+ Arrays.toString(message));

    }
}