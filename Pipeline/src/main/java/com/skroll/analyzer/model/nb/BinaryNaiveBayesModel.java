package com.skroll.analyzer.model.nb;

import java.util.*;

/**
 * Created by saurabh on 12/21/14.
 */
public class BinaryNaiveBayesModel {

    public static final double PRIOR_COUNT = 100;
    public static final boolean USE_FIRST_CHAR = true;


    int[] categoryCount;
    Map<String,Integer>[] wordCounts;


    public BinaryNaiveBayesModel() {
        this.categoryCount = new int[2];
        this.wordCounts = new HashMap[2];
        this.wordCounts[0] = new HashMap<String, Integer>();
        this.wordCounts[1] = new HashMap<String, Integer>();

    }

    public void incrementCategory(int categoryType) {
        this.categoryCount[categoryType]++;
    }

    public void addWord(int category, String word){
        Integer c = wordCounts[category].get(word);

        if (c == null) {
            c = 0;
        }
        wordCounts[category].put(word, c + 1);
    }

    double classProbability(int category){
        return (categoryCount[category]+PRIOR_COUNT)/(categoryCount[0]+categoryCount[1]+PRIOR_COUNT*2);
    }

    double wordCount(int category, String word){
        Integer c=wordCounts[category].get(word);
        if (c==null) c=0;
        return c+PRIOR_COUNT * classProbability(category);

    }

    public double inferJointProbability(int category, String[] words){
        double p = classProbability(category);
        for (String w:words){
            p=p* wordCount(category, w)/(categoryCount[category]+PRIOR_COUNT);
        }
        return p;
    }

    public double inferLogJointProbability(int category, String[] words){
        double logp = Math.log(categoryCount[category]+PRIOR_COUNT)- Math.log(categoryCount[0] + categoryCount[1] + PRIOR_COUNT * 2);
        for (String w:words){
            logp+= Math.log(wordCount(category, w))-Math.log(categoryCount[category]+PRIOR_COUNT);
        }
        return logp;
    }


    public double inferJointProbabilityWords(String[] words){
        return inferJointProbability(0, words)+inferJointProbability(1,words);
    }

    public double inferCategoryProbability(String[] words){
        double p0 = inferJointProbability(0, words), p1 = inferJointProbability(1,words);
        return p1 / (p0+p1);
    }
    public double inferCategoryProbabilityMoreStable(String[] words){
        double logp0 = inferLogJointProbability(0, words), logp1 = inferLogJointProbability(1, words);


        return 1/(Math.exp(logp0-logp1)+1);
    }

    public String toString(){
        String s="";
        s+="categoryCount: "+categoryCount[0]+", "+categoryCount[1]+"\n";
        for (int i=0;i<2;i++) {
            s+=("category "+i+":\n");
            s+=("---------------------------------------------------------------------\n");
            for (String word : wordCounts[i].keySet()) {
                s+=(word + "~~" + wordCounts[i].get(word)+"\n");
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
    public String showWordsImportance(String word){
        //System.out.println(categoryCount[0]+" "+categoryCount[1]);
        double []classProb = new double[2];
        for (int i=0;i<2;i++) classProb[i] = classProbability(i);
        Double[] count=new Double[2];
        for (int i=0;i<2;i++){
            Integer c=wordCounts[i].get(word);
            if (c==null) c=0;
            count[i]= c+PRIOR_COUNT*classProb[i];
        }
        double score = (double)count[1]/count[0]*(categoryCount[0]+PRIOR_COUNT)/(categoryCount[1]+PRIOR_COUNT);
        return (new AbstractMap.SimpleEntry<String, List<Double>>(word, Arrays.asList(score, (double) count[0], (double) count[1])) + " ");

    }



}
