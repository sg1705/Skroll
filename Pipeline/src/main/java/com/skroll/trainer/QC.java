package com.skroll.trainer;

import com.skroll.classifier.Category;

import java.util.Arrays;
import java.util.List;

/**
 * Created by saurabhagarwal on 6/6/15.
 */
public class QC {
    String docName;
    double QCScore;
    List<Stats> stats = Arrays.asList(new Stats(Category.DEFINITION),new Stats(Category.TOC_1),new Stats(Category.TOC_2),new Stats(Category.TOC_3),new Stats(Category.TOC_4),new Stats(Category.TOC_5 ));

    public static class Stats {
        int categoyId;
        int overallOccurance;
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
                    ", typeAError=" + typeAError +
                    ", typeBError=" + typeBError +
                    '}';
        }
    }
}
