package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariableType;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

public class ProbabilityDiscreteNodeTest {

    static DiscreteNode fNode, pNode, dNode;

    @BeforeClass
    public static void setUpOnce() throws  Exception{

        DiscreteNode tdNode = NodeTrainingHelper.createTrainingDiscreteNode(
                Arrays.asList(RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES));
        DiscreteNode tpNode = NodeTrainingHelper.createTrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_HAS_DEFINITION));
        DiscreteNode tfNode = NodeTrainingHelper.createTrainingDiscreteNode(
                Arrays.asList(RandomVariableType.PARAGRAPH_STARTS_WITH_QUOTE,
                        RandomVariableType.PARAGRAPH_HAS_DEFINITION,
                        RandomVariableType.DOCUMENT_DEFINITIONS_IN_QUOTES),
                Arrays.asList(tpNode, tdNode)
        ) ;





         pNode = NodeInferenceHelper.createLogProbabilityDiscreteNode(tpNode);
         dNode = NodeInferenceHelper.createLogProbabilityDiscreteNode(tdNode);
        fNode = NodeInferenceHelper.createLogProbabilityDiscreteNode(tfNode, Arrays.asList(pNode, dNode));
        fNode.setParameters(new double[]{0.1,0.8,0.3,0.7,0.5,0.5,0.6,0.4});



    }
//    public void testCreateProbabilityNodeFromTrainingNode(){
//        TrainingDiscreteNode node;
//        node = new TrainingDiscreteNode(
//                Arrays.asList(RandomVariableType.PARAGRAPH_NUMBER_TOKENS));
//        node.setParents(new TrainingDiscreteNode[0]);
//
//        ProbabilityDiscreteNode pNode = new ProbabilityDiscreteNode(node);
//    }

//    public void testGetProbabilities() throws Exception {
//
//        System.out.println("test");
//
//    }
//
    @Test
    public void testSumOutOtherNodesWithObservation() throws Exception {
        //fNode.setParents( Arrays.asList(pNode).toArray(new DiscreteNode[2]));
        //fNode.setParameters(new double[]{0.1,0.8,0.3,0.7});
        System.out.println(fNode);
        fNode.setObservation(1);
        double[] message = NodeInferenceHelper.sumOutOtherNodesWithObservation(fNode,pNode);
        System.out.println("with observation 1, message="+ Arrays.toString(message));

    }
}