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
        int typeAError;
        int typeBError;
        public Stats(int categoyId){
            this.categoyId = categoyId;
        }

        @Override
        public String toString() {
            return "Stats{" +
                    "categoyId=" + categoyId +
                    ", overallOccurance=" + overallOccurance +
                    ", noOccurance=" + noOccurance +
                    ", typeAError=" + typeAError +
                    ", typeBError=" + typeBError +
                    ", qcScore=" + getQCScore() +
                    '}';
        }
        public double getQCScore(){
            if (overallOccurance <1){
                return 0;
            }
            return (((typeAError*100)/(overallOccurance))*2 +((typeBError*100)/(overallOccurance)))/3;
        }
    }

}
