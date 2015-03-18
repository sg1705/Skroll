package com.skroll.analyzer.model.bn.inference;

import com.skroll.analyzer.model.bn.node.ProbabilityDiscreteNode;

/**
 * This is a very specialized belif propagation just for our network.
 * In our simple situation, can put each node in one cluster
 * Created by wei2learn on 3/11/2015.
 */
public class BNInference {
    ProbabilityDiscreteNode[] nodes;

    /**
     * specialized method for updating beliefs with messages of the same sizes
     * @param originalBelief
     * @param messages
     * @return
     */
    public static double[] getBelief(double[] originalBelief, double[][] messages){
        double[] newBelief = originalBelief.clone();
        for (int i=0; i<originalBelief.length; i++){
            for (int j=0; j<messages.length; j++)
                newBelief[i] *= messages[i][j];
        }
        return newBelief;
    }

    public static double[] normalize(double[] vals, double weight){
        double[] probs = new double[vals.length];
        double sum=0;
        for (double p:vals) sum+=p;
        for (int i=0;i<probs.length;i++) probs[i]= vals[i]/sum*weight;
        return probs;
    }
}
