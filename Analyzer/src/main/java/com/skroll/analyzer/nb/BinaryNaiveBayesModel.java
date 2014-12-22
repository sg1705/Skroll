package com.skroll.analyzer.nb;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by saurabh on 12/21/14.
 */
public class BinaryNaiveBayesModel {

    public static int CATEGORY_POSITIVE = 1;
    public static int CATEGORY_NEGATIVE = 0;
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

}
