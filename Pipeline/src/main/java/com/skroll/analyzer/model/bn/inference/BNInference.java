package com.skroll.analyzer.model.bn.inference;

import com.skroll.analyzer.model.bn.node.ProbabilityDiscreteNode;

/**
 * This is a very specialized belif propagation just for our network.
 * In our simple situation, can put each node in one cluster
 * Created by wei2learn on 3/11/2015.
 */
public class BNInference {
    ProbabilityDiscreteNode[] nodes;


    double[] messageToCategoryNode(ProbabilityDiscreteNode fNode, ProbabilityDiscreteNode cNode){
        return fNode.sumOutNodesWithObservationExcept(cNode);
    }
}
