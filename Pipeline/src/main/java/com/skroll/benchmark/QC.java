package com.skroll.benchmark;

import com.skroll.classifier.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by saurabhagarwal on 6/6/15.
 */
public class QC {

    Map<Integer, Stats> stats = new HashMap<>();

    public QC() {
        stats.put(Category.TOC_1, new Stats(Category.TOC_1));
        stats.put(Category.TOC_2, new Stats(Category.TOC_2));
        /*
        for (int category : Category.getCategoriesExcludingNONE()){
            stats.add(new Stats(category));
        }
        */
    }
    public void calculateQCScore() {
        for (Stats stat : stats.values()){
            stat.calculateQCScore();
        }
    }
    @Override
    public String toString() {
        return "QC{" +
                "stats=" + stats +
                '}';
    }

    public void add(QC qc){
        for (Integer category : this.stats.keySet()){
            Stats stats = this.stats.get(category);
            Stats inputStats = qc.stats.get(category);
            stats.type1Error += inputStats.type1Error;
            stats.type2Error += inputStats.type2Error;
            stats.overallOccurance += inputStats.overallOccurance;
            stats.postClassificationOccurance += inputStats.postClassificationOccurance;
        }
    }
    public static class Stats {
        int categoyId;
        double qcScore;
        int type1Error;
        int type2Error;
        int overallOccurance;
        int postClassificationOccurance;
        public Stats(int categoyId){
            this.categoyId = categoyId;
        }

        @Override
        public String toString() {
            return "Stats{" +
                    "categoyId=" + categoyId +
                    ", qcScore=" + qcScore +
                    ", type1Error=" + type1Error +
                    ", type2Error=" + type2Error +
                    ", overallOccurance=" + overallOccurance +
                    ", postClassificationOccurance=" + postClassificationOccurance +
                    '}';
        }
        public double calculateQCScore(){
            int tempOverallOccurance = overallOccurance;
            if (tempOverallOccurance <1){
                tempOverallOccurance= 1;
            }
            int tmpPostClassificationOccurance = postClassificationOccurance;
            if (tmpPostClassificationOccurance <1){
                tmpPostClassificationOccurance= 1;
            }
            qcScore= 100 - (((type1Error *100)/(tmpPostClassificationOccurance))*2 +((type2Error*100)/(tempOverallOccurance)))/3;
            return qcScore;
        }
    }
}
