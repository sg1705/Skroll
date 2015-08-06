package com.skroll.benchmark;

import com.skroll.classifier.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabhagarwal on 6/6/15.
 */
public class QC {

    List<Stats> stats = new ArrayList<>();

    public QC() {
        for (int category : Category.getCategoriesExcludingNONE()){
            stats.add(new Stats(category));
        }
    }
    public void calculateQCScore() {
        for (Stats stat : stats){
            stat.calculateQCScore();
        }
    }
    @Override
    public String toString() {
        return "QC{" +
                ", stats=" + stats +
                '}';
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
            if (overallOccurance <1){
                overallOccurance= 1;
            }
            if (postClassificationOccurance <1){
                postClassificationOccurance= 1;
            }
            qcScore= 100 - (((type1Error *100)/(postClassificationOccurance))*2 +((type2Error*100)/(overallOccurance)))/3;
            return qcScore;
        }
    }

}
