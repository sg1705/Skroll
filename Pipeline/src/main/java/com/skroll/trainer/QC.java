package com.skroll.trainer;

import com.skroll.classifier.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabhagarwal on 6/6/15.
 */
public class QC {

    List<Stats> stats = new ArrayList<>();

    public QC() {
        for (int category : Category.getCategories()){
            stats.add(new Stats(category));
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
        int overallOccurance;
        int noOccurance;
        int type1Error;
        int type2Error;
        public Stats(int categoyId){
            this.categoyId = categoyId;
        }

        @Override
        public String toString() {
            return "Stats{" +
                    "categoyId=" + categoyId +
                    ", type1Error=" + type1Error +
                    ", noOccurance=" + noOccurance +
                    ", type2Error=" + type2Error +
                    ", overallOccurance=" + overallOccurance +
                    ", qcScore=" + getQCScore() +
                    '}';
        }
        public double getQCScore(){
            if (overallOccurance <1){
                overallOccurance= 1;
            }
            if (noOccurance <1){
                noOccurance= 1;
            }
            return 100 - (((type1Error *100)/(noOccurance))*2 +((type2Error*100)/(overallOccurance)))/3;
        }
    }

}
