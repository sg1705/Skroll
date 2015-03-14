package com.skroll.document.annotation;

import com.google.common.collect.Lists;
import com.skroll.document.CoreMap;

import java.util.List;

/**
 * Created by saurabhagarwal on 3/8/15.
 */
public class TrainingWeightAnnotationHelper {
    public final static int DEFINITION =0;
    public final static int TOC =1;
    public final static int NONE =2;

    public static void updateTrainingWeight(CoreMap paragraph, int index, float userWeight){
        List<Float>  userWeightList = paragraph.get(CoreAnnotations.TrainingWeightAnnotation.class);
        if (userWeightList == null) {
            userWeightList = Lists.newArrayList((float)0,(float)0,(float)0);
            paragraph.set(CoreAnnotations.TrainingWeightAnnotation.class, userWeightList);
        }
        userWeightList.set(index, userWeightList.get(index) + userWeight);

    }
}
