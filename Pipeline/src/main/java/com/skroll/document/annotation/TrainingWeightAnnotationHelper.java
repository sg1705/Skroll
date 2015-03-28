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
    public final static int LATEST_WEIGHT_INDEX =3;
    public final static int INDEX =3;

    public static void updateTrainingWeight(CoreMap paragraph, int index, float userWeight){
        List<Float>  userWeightList = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
        if (userWeightList == null) {
            //First three float are previously trained weight - Definition, TOC and NONE
            //Second set of three floats are currently trained weight
            userWeightList = Lists.newArrayList((float)0,(float)0,(float)0,(float)0,(float)0,(float)0);
            paragraph.set(CoreAnnotations.TrainingWeightAnnotationFloat.class, userWeightList);
        }

        userWeightList.set(DEFINITION,userWeightList.get(DEFINITION + LATEST_WEIGHT_INDEX));
        userWeightList.set(TOC,userWeightList.get(TOC+LATEST_WEIGHT_INDEX));
        userWeightList.set(NONE,userWeightList.get(NONE+LATEST_WEIGHT_INDEX));

        if(paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)){
            userWeightList.set(index+LATEST_WEIGHT_INDEX, userWeight);
        } else {
            userWeightList.set(index+LATEST_WEIGHT_INDEX, userWeightList.get(index) + userWeight);
        }

    }
}
