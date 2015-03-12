package com.skroll.analyzer.model.bn;

import com.skroll.analyzer.model.bn.node.DiscreteNode;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.analyzer.model.bn.node.TrainingDiscreteNode;
import com.skroll.analyzer.model.bn.node.TrainingWordNode;
import com.skroll.analyzer.model.bn.node.WordNode;

import java.util.Arrays;
import java.util.List;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class TrainingNaiveBayes extends NaiveBayes {


    TrainingDiscreteNode trainingCategoryNode;
    TrainingDiscreteNode[] trainingFeatureNodeArray;

    TrainingDiscreteNode[] trainingDiscreteNodeArray;
    TrainingWordNode trainingWordNode;
    // for training with complete observed data, we can set observation on all nodes,
    // then make each node update its frequency count

    // assuming there is a documentFeature for each feature, so the sizes of the two lists passed in should match.
    public TrainingNaiveBayes(RandomVariableType categoryVar, List<RandomVariableType> featureVarList,
                              List<RandomVariableType> documentFeatureVarList) {
        categoryNode = new TrainingDiscreteNode(Arrays.asList(categoryVar));
        featureNodeArray = new TrainingDiscreteNode[ featureVarList.size()];
        for (int i=0; i<featureVarList.size(); i++) {
            featureNodeArray[i] = new TrainingDiscreteNode(
                    Arrays.asList(featureVarList.get(i), categoryVar));
        }

        wordNode = new TrainingWordNode((TrainingDiscreteNode)categoryNode);
        generateParentsAndChildren();

        // put all nodes in a single array for simpler update.
        int i=0;
        discreteNodeArray[i++] = categoryNode;
        for (DiscreteNode node: featureNodeArray){
            discreteNodeArray[i++] = node;
        }
        trainingDiscreteNodeArray = (TrainingDiscreteNode[]) discreteNodeArray;
        trainingWordNode = (TrainingWordNode) wordNode;
        trainingCategoryNode = (TrainingDiscreteNode) categoryNode;
        trainingFeatureNodeArray = (TrainingDiscreteNode[]) featureNodeArray;

    }


    /**
     * Assume the order of the observed values matches the order of the nodes.
     * Assume the order of the nods is [category, features, documentFeatures]
     * @param tuple
     */
//    public void addSample(SimpleDataTuple tuple){
//        setObservation(tuple);
//        for (TrainingDiscreteNode node: trainingDiscreteNodeArray){
//            node.updateCount();
//        }
//        trainingWordNode.updateCount();
//        clearObservation(); // probably unnecessary
//    }
//
    public void addSample(SimpleDataTuple tuple){
        setObservation(tuple);
        for (DiscreteNode node: discreteNodeArray){
            ((TrainingDiscreteNode) node).updateCount();
        }
        ((TrainingWordNode) wordNode).updateCount();
        clearObservation(); // probably unnecessary
    }
    public TrainingDiscreteNode[] getTrainingDiscreteNodeArray() {
        return trainingDiscreteNodeArray;
    }

    public TrainingDiscreteNode getTrainingCategoryNode() {
        return trainingCategoryNode;
    }

    public TrainingWordNode getTrainingWordNode() {
        return trainingWordNode;
    }
//
//    public void updateProbabilities(){
//        for (DiscreteNode node:discreteNodeArray){
//            node.updateProbabilities();
//        }
//        wordNode.updateProbabilities();
//    }
//
//    double classProbability(int category){
//        return categoryNode.getProbability(category);
//    }
//
//    public double inferJointProbability(int category, String[] words, int[] features){
//        double p = classProbability(category);
//        for (String w:words){
//            p=p* wordCount(category, w)/(categoryCount[category]);
//        }
//        if (features==null) return p;
//
//        for (int featureNumber =0; featureNumber<numberFeatures; featureNumber++){
//            p = p* featureCount(category, featureNumber, features[featureNumber]) / categoryCount[category];
//        }
//        //for (int f: features) p = p* featureCount(category, f) / categoryCount[category];
//        return p;
//    }
//
//
//    // return joint probabilities for each category
//    public double[] inferLogJointProbability(String[] words, int[] features){
//        double[] logProbs = new double[numberCategories];
//        for (int i=0; i<numberCategories;i++){
//            logProbs[i] = inferLogJointProbability(i, words, features);
//        }
//        return logProbs;
//    }
//
//    public double inferLogJointFeaturesProbabilityGivenCategory(int category, String[] words, int[] features){
//        double logp = 0;
//
//        for (String w:words) logp += Math.log(wordCount(category, w));
//        logp -= words.length * Math.log(categoryCount[category]);
//
//        if (features==null) return logp;
//
//        for (int featureNumber =0; featureNumber<numberFeatures; featureNumber++){
//            logp += Math.log(featureCount(category, featureNumber, features[featureNumber]));
//        }
//        //for (int f: features) logp = logp* Math.log(featureCount(category, f));
//        logp -= features.length * Math.log(categoryCount[category]);
//
//        return logp;
//    }
//
//    public double[] inferLogJointFeaturesProbabilityGivenCategories(String[] words, int[] features){
//        double [] logProbs = new double[numberCategories];
//        for (int c=0;c<numberCategories;c++){
//            logProbs[c] = inferLogJointFeaturesProbabilityGivenCategory(c, words, features);
//        }
//        return logProbs;
//    }
//
//    public double inferLogJointProbability(int category, String[] words, int[] features){
//        double logp = Math.log(categoryCount[category])- Math.log(totalCategoryCount);
//
//        logp += inferLogJointFeaturesProbabilityGivenCategory(category, words, features);
//        return logp;
//    }
//
//
//    public double inferJointProbabilityWords(String[] words, int [] features){
//        double prob=0;
//        for (int category = 0; category<numberCategories; category ++)
//            prob += inferJointProbability(0, words, features);
//        return prob;
//    }
//
//    public double[] inferCategoryProbabilities(String[] words, int[] features){
//        double[] probs = new double[numberCategories];
//        double jointProbOfFeatures=0;
//        for (int category =0; category < numberCategories; category++){
//            probs[category] = inferJointProbability(category, words, features);
//            jointProbOfFeatures += probs[category];
//        }
//
//        // normalize the joint probabilities to get the conditional probabilities
//        for (int category =0; category < numberCategories; category++){
//            probs[category] /= jointProbOfFeatures;
//        }
//
//        return probs;
//    }
//
//    public double inferCategoryProbability(int category, String[] words, int[] features){
//        double[] probs = inferCategoryProbabilities(words, features);
//        return probs[category];
//    }
//
//    // for stability, use the formula
//    // p0 / (p0+...+pn) = 1/(p0/p0+ p1/p0 + p2/p0 + ... + pn/p0)
//    public double inferCategoryProbabilityMoreStable(int category, String[] words, int[] features){
//        double[] logProbs = inferLogJointProbability(words, features);
//
//        double denominator=0;
//        for (int c=0; c < numberCategories; c++) {
//            denominator += Math.exp(logProbs[c] - logProbs[category]);
//        }
//
//        return 1/denominator;
//
//    }
//
//    public double[] inferCategoryProbabilitiesMoreStable(String[] words, int[] features){
//        double[] logProbs = inferLogJointProbability(words, features);
//        double[] result = new double[numberCategories];
//
//        double denominator=0;
//        for (int category=0; category<numberCategories; category++){
//            for (int c=0; c < numberCategories; c++) {
//                result[category] += Math.exp(logProbs[c] - logProbs[category]);
//            }
//            result[category] = 1/result[category];
//        }
//
//
//        return result;
//
//    }
//
//    public  int mostLikelyCategory(DataTuple tuple) {
//        return mostLikelyCategory(tuple.getTokens(), tuple.getFeatures());
//    }
//
//
//     public  int mostLikelyCategory(String[] words, int[] features){
//        double max=-Double.MAX_VALUE;
//        int maxCat =0;
//        for (int c =0; c < numberCategories; c++){
//            double logProb = inferLogJointProbability(c, words, features);
//            if (logProb > max){
//                max = logProb;
//                maxCat = c;
//            }
//        }
//        return maxCat;
//    }
//
//    @Override
//
//    public String toString(){
//        String s="";
//        s+= "totalCategoryCount: "+totalCategoryCount;
//        s+="\n";
//        for (int i=0;i<numberCategories;i++) {
//            s+=("category "+i+":\n");
//            s+="categoryCount: "+categoryCount[i]+"\n";
//            s+=("---------------------------------------------------------------------\n");
//            for (String word : wordCounts[i].keySet()) {
//                s+=(word + "=" + wordCounts[i].get(word) + " ");
//            }
//            s+="\n";
//            for (int[] featureValueCounts :categoryFeatureValueCounts.get(i)) {
//                s += "features: " + Arrays.toString(featureValueCounts);
//                s += "\n";
//            }
//        }
//        return s;
//    }
//
//
//
//    public String showWordsImportance(){
//        System.out.println(categoryCount[0]+" "+categoryCount[1]);
//        SortedSet<Map.Entry<String, List<Double>>> sortedset = new TreeSet<Map.Entry<String, List<Double>>>(
//                new Comparator<Map.Entry<String, List<Double>>>() {
//                    @Override
//                    public int compare(Map.Entry<String, List<Double>> e1,
//                                       Map.Entry<String, List<Double>> e2) {
//                        int c= e1.getValue().get(0).compareTo(e2.getValue().get(0));
//                        if (c==0) return -e1.getKey().compareTo(e2.getKey());
//                        return -e1.getValue().get(0).compareTo(e2.getValue().get(0));                    }
//                });
//        double []classProb = new double[2];
//        for (int i=0;i<2;i++) classProb[i] = classProbability(i);
//        for (int category=0;category<2;category++) {
//            for (String k : wordCounts[category].keySet()) {
//                Double[] count=new Double[2];
//                for (int i=0;i<2;i++){
//                    Integer c=wordCounts[i].get(k);
//                    if (c==null) c=0;
//                    count[i]= c+PRIOR_COUNT*classProb[i];
//                }
//                double score = (double)count[1]/count[0]*(categoryCount[0]+PRIOR_COUNT)/(categoryCount[1]+PRIOR_COUNT);
//                sortedset.add(new AbstractMap.SimpleEntry<String, List<Double>>(k,Arrays.asList(score,(double)count[0],(double)count[1])));
//            }
//        }
//        return sortedset.toString();
//    }
//
//    public String showWordsImportance(int category){
//        //System.out.println(categoryCount[0]+" "+categoryCount[1]);
//        SortedSet<Map.Entry<String, List<Double>>> sortedset = new TreeSet<Map.Entry<String, List<Double>>>(
//                new Comparator<Map.Entry<String, List<Double>>>() {
//                    @Override
//                    public int compare(Map.Entry<String, List<Double>> e1,
//                                       Map.Entry<String, List<Double>> e2) {
//                        int c= e1.getValue().get(0).compareTo(e2.getValue().get(0));
//                        if (c==0) return -e1.getKey().compareTo(e2.getKey());
//                        return -e1.getValue().get(0).compareTo(e2.getValue().get(0));                    }
//                });
//
//        // collect all words in one set
//        Set<String> words = new HashSet<String>();
//        for (Map<String, Integer> m: wordCounts){
//            words.addAll( m.keySet());
//        }
//
//        for (String word: words){
//            double totalCount =0;
//            Double []counts = new Double[numberCategories];
//            for (int i=0; i<numberCategories;i ++){
//                counts[i] = wordCount(i, word);
//                totalCount += counts[i];
//            }
//            List<Double> vals =  new ArrayList<Double>(Arrays.asList(counts[category]/totalCount));
//            vals.addAll(Arrays.asList(counts));
//            sortedset.add(new AbstractMap.SimpleEntry<String, List<Double>>(word, vals));
//        }
//
//        return (double)categoryCount[category]/totalCategoryCount+" "+ Arrays.toString(categoryCount) +'\n' + sortedset.toString();
//    }
//
//    public double[] wordContributions(String word){
//
//        double []contributions = new double[numberCategories];
//        double totalCount =0;
//        for (int i=0; i<numberCategories;i ++){
//            contributions[i] = wordCount(i, word);
//            totalCount += contributions[i];
//        }
//        for (int i=0; i<numberCategories;i ++){
//            contributions[i] /= totalCount;
//        }
//        return contributions;
//
//    }

}
