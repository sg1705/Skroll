//package com.skroll.analyzer.model.bn.node;
//
//import com.fasterxml.jackson.annotation.JsonIgnore;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * WordNode stores the random variable representing the distribution of words.
// * When it is used, it actually process all words observations in a data tuple.
// * Created by wei2learn on 3/1/2015.
// */
//public class TrainingWordNode extends WordNode {
//    private static final double PRIOR_COUNT = 100;
//
//    //Map<String, double[]> parameters;
//
//    TrainingWordNode(){
//    }
//
//    public TrainingWordNode(TrainingDiscreteNode parent){
//        super(parent);
//        //parameters = parameters;
//    }
//
//    public TrainingWordNode( TrainingWordNode trainingNode){
//        parameters = trainingNode.getCopyOfParameters();
//    }
//
//    public void updateCount(){
//        updateCount(1);
//    }
//
//    public void updateCount(double weight){
//        int parentObservation = parent.getObservation();
//        for (String w: observedWords)
//            updateCount(w, parentObservation, weight);
//    }
//
//    void updateCount(String word, int parentValue){
//        updateCount(word, parentValue, 1);
//    }
//
//    void updateCount(String word, int parentValue, double weight){
//        double[] counts = parameters.get(word);
//        if (counts == null) {
//            counts = new double[ parent.getVariable().getFeatureSize() ];
//            //Arrays.fill(parameters, PRIOR_COUNT);
//            parameters.put(word, counts);
//        }
//        counts[parentValue] += weight;
//    }
//
//    /**
//     * convert parameters to probabilities
//     */
//    @JsonIgnore
//    public Map<String, double[]> getProbabilities(){
//        double [] priorCounts = ((TrainingDiscreteNode) parent).getPriorCount(PRIOR_COUNT);
//        Map<String, double[]> probs = new HashMap<>();
//        int numValues = parent.getVariable().getFeatureSize();
//
//        for (String w: parameters.keySet()){
//            double[] p = new double[ parent.getVariable().getFeatureSize()  ];
//            //double sum=0;
//            //for (int j=0; j<numValues; j++) sum += parameters.get(w)[j];
//            for (int j=0; j<numValues; j++) p[j] = (priorCounts[j] +
//                    parameters.get(w)[j])/ parent.getParameter(j);
//            probs.put(w,p);
//        }
//        return probs;
//    }
//
//    @JsonIgnore
//    public Map<String, double[]> getLogProbabilities(){
//        double [] priorCounts = ((TrainingDiscreteNode) parent).getPriorCount(PRIOR_COUNT);
//        Map<String, double[]> probs = new HashMap<>();
//        int numValues = parent.getVariable().getFeatureSize();
//
//        for (String w: parameters.keySet()){
//            double[] p = new double[ parent.getVariable().getFeatureSize()  ];
//            //double sum=0;
//            //for (int j=0; j<numValues; j++) sum += parameters.get(w)[j];
//
//
//            //hack for testing purpose
//            if (parameters.get(w)[0]+ parameters.get(w)[1] <1) continue;
//            for (int j=0; j<numValues; j++) p[j] = Math.log((0.01 +
//                    parameters.get(w)[j])/ parent.getParameter(j));
//
////            for (int j=0; j<numValues; j++) p[j] = Math.log((priorCounts[j] +
////                    parameters.get(w)[j])/ parent.getParameter(j));
//            probs.put(w,p);
//        }
//        return probs;
//    }
//
//
//}
