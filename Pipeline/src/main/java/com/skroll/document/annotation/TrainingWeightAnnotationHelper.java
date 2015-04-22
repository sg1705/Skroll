package com.skroll.document.annotation;

import com.google.common.collect.Lists;
import com.skroll.analyzer.model.RandomVariableType;
import com.skroll.document.CoreMap;

import java.util.List;

/**
 * Created by saurabhagarwal on 3/8/15.
 */
public class TrainingWeightAnnotationHelper {
    public final static int NONE =0;
    public final static int DEFINITION =1;
    public final static int TOC =2;
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

    }


    public static void setTrainingWeight(CoreMap paragraph, int index, float userWeight){
        List<Float>  userWeightList = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
        if (userWeightList == null) {
            //First three float are previously trained weight - Definition, TOC and NONE
            //Second set of three floats are currently trained weight
            userWeightList = Lists.newArrayList((float)0,(float)0,(float)0,(float)0,(float)0,(float)0);
            paragraph.set(CoreAnnotations.TrainingWeightAnnotationFloat.class, userWeightList);
        }

        if(paragraph.containsKey(CoreAnnotations.IsTrainerFeedbackAnnotation.class)){
            for (int i=LATEST_WEIGHT_INDEX; i<LATEST_WEIGHT_INDEX*2;i++)
                userWeightList.set(i,(float)0);
            userWeightList.set(index+LATEST_WEIGHT_INDEX, userWeight);
        } else {
            userWeightList.set(index+LATEST_WEIGHT_INDEX, userWeightList.get(index) + userWeight);
        }

    }

    public static void clearOldTrainingWeight(CoreMap paragraph){
        List<Float>  userWeightList = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
        if (userWeightList != null) {
            for (int i = 0; i < INDEX; i++)
                userWeightList.set(i, (float) 0);
        }

    }
    // method converts multi-class weight to binary class weight.
    // todo: this method is temporarily used to get the weights. Should probably be improved later to handle multi-class and others.
    public static double[][] getParagraphWeight(CoreMap para, RandomVariableType paraType){
        List<Float>  weightList = para.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
        double[][] weights = new double[2][paraType.getFeatureSize()];
        weights[0][0] = weightList.get(NONE);
        weights[1][0] = weightList.get(NONE+LATEST_WEIGHT_INDEX);

        switch (paraType){
            case PARAGRAPH_HAS_TOC:
                weights[0][1] = weightList.get(TOC);
                weights[1][1] = weightList.get(TOC+LATEST_WEIGHT_INDEX);
                return weights;
            case PARAGRAPH_HAS_DEFINITION:
                weights[0][1] = weightList.get(DEFINITION);
                weights[1][1] = weightList.get(DEFINITION+LATEST_WEIGHT_INDEX);
                return weights;
        }
        return weights;
    }
}
