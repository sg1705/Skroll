package com.skroll.analyzer.model.bn.inference;


/**
 * This is a very specialized belif propagation just for our network.
 * In our simple situation, can put each node in one cluster
 * Created by wei2learn on 3/11/2015.
 */
public class BNInference {

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

//    public static double[] normalize(double[] vals){
//        double sum=0;
//        for (double p:vals) sum+=p;
//        double[] probs = new double[vals.length];
//
//        if (sum!=0)
//            for (int i=0;i<probs.length;i++) probs[i]= vals[i]/sum;
//        return probs;
//    }

    public static void normalizeInplace(double[] vals){
        double sum=0;
        for (double p:vals) sum+=p;
        if (sum!=0)
            for (int i=0;i<vals.length;i++) vals[i]= vals[i]/sum;
    }

    public static double[] normalize(double[] vals, double weight){
        double sum=0;
        for (double p:vals) sum+=p;
        double[] probs = new double[vals.length];

        if (sum!=0)
            for (int i=0;i<probs.length;i++) probs[i]= vals[i]/sum*weight;
        return probs;
    }
    public static double[] normalize(Float[] vals, double weight){
        double sum=0;
        for (double p:vals) sum+=p;
        double[] probs = new double[vals.length];

        if (sum!=0)
            for (int i=0;i<probs.length;i++) probs[i]= vals[i]/sum*weight;
        return probs;
    }

    /**
     * make the maximum to be 0.
     * @param vals
     */
    public static void normalizeLog(double[] vals){
        double max=vals[0];
        for (int i=1;i<vals.length;i++) if (vals[i]>max) max=vals[i];
        for (int i=0;i<vals.length;i++) vals[i] = vals[i] - max;
    }


    public static void log(double[] vals){
        for (int i=0;i<vals.length;i++)
            vals[i] = Math.log(vals[i]);
    }

    public static void exp(double[] vals){
        for (int i=0;i<vals.length;i++)
            vals[i] = Math.exp(vals[i]);
    }

    public static void convertLogBeliefToProb(double[] beliefs){
        normalizeLog(beliefs);
        convertNormalizedLogBeliefToProb(beliefs);
    }

    public static void convertNormalizedLogBeliefToProb(double[] beliefs){
        exp(beliefs);
        normalizeInplace(beliefs);
    }

    public static void convertLogBeliefArrayToProb(double[][] beliefsArray){
        for (double[] belief: beliefsArray){
            convertLogBeliefToProb(belief);
        }
    }

    public static int maxIndex(double[] vals) {
        if (vals == null || vals.length == 0) return -1;
        int maxI = 0;
        for (int i = 1; i < vals.length; i++) {
            if (vals[i] > vals[maxI]) {
                maxI = i;
            }
        }

        return maxI;
    }
}
