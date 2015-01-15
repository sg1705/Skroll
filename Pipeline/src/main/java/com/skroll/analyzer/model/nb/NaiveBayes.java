package com.skroll.analyzer.model.nb;

import java.util.*;

/**
 * Created by wei2learn on 1/3/2015.
 */
public class NaiveBayes {

    public static final double PRIOR_COUNT = 100;
    public static final int DEFAULT_NUMBER_CATEGORIES = 2;
    public static final int DEFAULT_NUMBER_FEATURES = 0;


    int numberCategories;
    int numberFeatures;
    int[] featureSizes;
    int[] categoryCount;

    int totalCategoryCount; // do we need to use long instead of int?
    // int or long is better than double for increment by 1's used to update counts


    //int [][] featureCounts; // count
    List<List<int[]>> categoryFeatureValueCounts;
    Map<String,Integer>[] wordCounts;

    public NaiveBayes() {
        this(DEFAULT_NUMBER_CATEGORIES, new int[0]);
    }

    public NaiveBayes(int numCategories, int []featureSizes) {
        this.featureSizes = featureSizes;
        this.numberCategories = numCategories;
        this.numberFeatures = featureSizes.length;
        this.categoryCount = new int[numCategories];
        this.wordCounts = new HashMap[numCategories];
        //this.featureCounts = new int[numCategories][numFeatures];
        this.categoryFeatureValueCounts = new ArrayList<List<int[]>>();

        this.totalCategoryCount = (int) PRIOR_COUNT * numCategories;
        for (int i=0; i<numCategories; i++) {
            this.categoryCount[i] = (int)PRIOR_COUNT;
            this.wordCounts[i] = new HashMap<String, Integer>();
            List< int[] > featureValueCounts = new ArrayList<int[]>();
            for (int f =0; f<numberFeatures; f++){
                int []valueCounts = new int[featureSizes[f]];
                featureValueCounts.add(valueCounts);
            }
            this.categoryFeatureValueCounts.add(featureValueCounts);
        }
    }

    public void addSample(DataTuple tuple){
        int category = tuple.getCategory();
        this.categoryCount[category]++;
        totalCategoryCount++;
        String[] words = tuple.getTokens();
        int[] features = tuple.getFeatures();
        for (String word: words) {
            Integer c = wordCounts[category].get(word);

            if (c == null) {
                c = 0;
            }
            wordCounts[category].put(word, c + 1);
        }
        if (features==null) return;
        for (int featureNumber=0; featureNumber<numberFeatures; featureNumber++){
            categoryFeatureValueCounts.get(category).get(featureNumber)[features[featureNumber]]++;
            //featureCounts[category][features[i]]++;
        }
    }

    double classProbability(int category){

        return (double)categoryCount[category]/totalCategoryCount;
    }

    double wordCount(int category, String word){
        Integer c=wordCounts[category].get(word);
        if (c==null) c=0;
        return c+PRIOR_COUNT * classProbability(category);

    }

    double featureCount(int category, int featureNumber, int featureValue){
        return categoryFeatureValueCounts.get(category).get(featureNumber)[featureValue]+
                PRIOR_COUNT * classProbability(category);

    }

    public double inferJointProbability(int category, String[] words, int[] features){
        double p = classProbability(category);
        for (String w:words){
            p=p* wordCount(category, w)/(categoryCount[category]);
        }
        if (features==null) return p;

        for (int featureNumber =0; featureNumber<numberFeatures; featureNumber++){
            p = p* featureCount(category, featureNumber, features[featureNumber]) / categoryCount[category];
        }
        //for (int f: features) p = p* featureCount(category, f) / categoryCount[category];
        return p;
    }



    public double inferLogJointProbability(int category, String[] words, int[] features){
        double logp = Math.log(categoryCount[category])- Math.log(totalCategoryCount);

        for (String w:words) logp += Math.log(wordCount(category, w));
        logp -= words.length * Math.log(categoryCount[category]);

        if (features==null) return logp;

        for (int featureNumber =0; featureNumber<numberFeatures; featureNumber++){
            logp += Math.log(featureCount(category, featureNumber, features[featureNumber]));
        }
        //for (int f: features) logp = logp* Math.log(featureCount(category, f));
        logp -= features.length * Math.log(categoryCount[category]);

        return logp;
    }


    public double inferJointProbabilityWords(String[] words, int [] features){
        double prob=0;
        for (int category = 0; category<numberCategories; category ++)
            prob += inferJointProbability(0, words, features);
        return prob;
    }

    public double[] inferCategoryProbabilities(String[] words, int[] features){
        double[] probs = new double[numberCategories];
        double jointProbOfFeatures=0;
        for (int category =0; category < numberCategories; category++){
            probs[category] = inferJointProbability(category, words, features);
            jointProbOfFeatures += probs[category];
        }

        // normalize the joint probabilities to get the conditional probabilities
        for (int category =0; category < numberCategories; category++){
            probs[category] /= jointProbOfFeatures;
        }

        return probs;
    }

    public double inferCategoryProbability(int category, String[] words, int[] features){
        double[] probs = inferCategoryProbabilities(words, features);
        return probs[category];
    }

    // for stability, use the formula
    // p0 / (p0+...+pn) = 1/(1+ p1/p0 + p2/p0 + ... + pn/p0)
    public double inferCategoryProbabilityMoreStable(int category, String[] words, int[] features){
        double[] logProbs = new double[numberCategories];
        double logJointProbOfFeatures=0;
        for (int c =0; c < numberCategories; c++){
            logProbs[c] = inferLogJointProbability(c, words, features);
        }

        double denominator=0;
        for (int c=0; c < numberCategories; c++) {
            denominator += Math.exp(logProbs[c] - logProbs[category]);
        }

        return 1/denominator;

    }

    public  int mostLikelyCategory(DataTuple tuple) {
        return mostLikelyCategory(tuple.getTokens(), tuple.getFeatures());
    }


     public  int mostLikelyCategory(String[] words, int[] features){
        double max=-Double.MAX_VALUE;
        int maxCat =0;
        for (int c =0; c < numberCategories; c++){
            double logProb = inferLogJointProbability(c, words, features);
            if (logProb > max){
                max = logProb;
                maxCat = c;
            }
        }
        return maxCat;
    }

    @Override

    public String toString(){
        String s="";
        s+= "totalCategoryCount: "+totalCategoryCount;
        s+="\n";
        for (int i=0;i<numberCategories;i++) {
            s+=("category "+i+":\n");
            s+="categoryCount: "+categoryCount[i]+"\n";
            s+=("---------------------------------------------------------------------\n");
            for (String word : wordCounts[i].keySet()) {
                s+=(word + "=" + wordCounts[i].get(word) + " ");
            }
            s+="\n";
            for (int[] featureValueCounts :categoryFeatureValueCounts.get(i)) {
                s += "features: " + Arrays.toString(featureValueCounts);
                s += "\n";
            }
        }
        return s;
    }



    public String showWordsImportance(){
        System.out.println(categoryCount[0]+" "+categoryCount[1]);
        SortedSet<Map.Entry<String, List<Double>>> sortedset = new TreeSet<Map.Entry<String, List<Double>>>(
                new Comparator<Map.Entry<String, List<Double>>>() {
                    @Override
                    public int compare(Map.Entry<String, List<Double>> e1,
                                       Map.Entry<String, List<Double>> e2) {
                        int c= e1.getValue().get(0).compareTo(e2.getValue().get(0));
                        if (c==0) return -e1.getKey().compareTo(e2.getKey());
                        return -e1.getValue().get(0).compareTo(e2.getValue().get(0));                    }
                });
        double []classProb = new double[2];
        for (int i=0;i<2;i++) classProb[i] = classProbability(i);
        for (int category=0;category<2;category++) {
            for (String k : wordCounts[category].keySet()) {
                Double[] count=new Double[2];
                for (int i=0;i<2;i++){
                    Integer c=wordCounts[i].get(k);
                    if (c==null) c=0;
                    count[i]= c+PRIOR_COUNT*classProb[i];
                }
                double score = (double)count[1]/count[0]*(categoryCount[0]+PRIOR_COUNT)/(categoryCount[1]+PRIOR_COUNT);
                sortedset.add(new AbstractMap.SimpleEntry<String, List<Double>>(k,Arrays.asList(score,(double)count[0],(double)count[1])));
            }
        }
        return sortedset.toString();
    }

    public String showWordsImportance(int category){
        //System.out.println(categoryCount[0]+" "+categoryCount[1]);
        SortedSet<Map.Entry<String, List<Double>>> sortedset = new TreeSet<Map.Entry<String, List<Double>>>(
                new Comparator<Map.Entry<String, List<Double>>>() {
                    @Override
                    public int compare(Map.Entry<String, List<Double>> e1,
                                       Map.Entry<String, List<Double>> e2) {
                        int c= e1.getValue().get(0).compareTo(e2.getValue().get(0));
                        if (c==0) return -e1.getKey().compareTo(e2.getKey());
                        return -e1.getValue().get(0).compareTo(e2.getValue().get(0));                    }
                });

        // collect all words in one set
        Set<String> words = new HashSet<String>();
        for (Map<String, Integer> m: wordCounts){
            words.addAll( m.keySet());
        }

        for (String word: words){
            double totalCount =0;
            Double []counts = new Double[numberCategories];
            for (int i=0; i<numberCategories;i ++){
                counts[i] = wordCount(i, word);
                totalCount += counts[i];
            }
            List<Double> vals =  new ArrayList<Double>(Arrays.asList(counts[category]/totalCount));
            vals.addAll(Arrays.asList(counts));
            sortedset.add(new AbstractMap.SimpleEntry<String, List<Double>>(word, vals));
        }

        return (double)categoryCount[category]/totalCategoryCount+" "+ Arrays.toString(categoryCount) +'\n' + sortedset.toString();
    }

    public double[] wordContributions(String word){

        double []contributions = new double[numberCategories];
        double totalCount =0;
        for (int i=0; i<numberCategories;i ++){
            contributions[i] = wordCount(i, word);
            totalCount += contributions[i];
        }
        for (int i=0; i<numberCategories;i ++){
            contributions[i] /= totalCount;
        }
        return contributions;

    }

}
