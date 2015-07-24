package com.skroll.document.annotation;

import com.skroll.analyzer.model.RandomVariable;
import com.skroll.classifier.Category;
import com.skroll.classifier.ClassifierFactory;
import com.skroll.classifier.ClassifierProto;
import com.skroll.document.CoreMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by saurabhagarwal on 3/8/15.
 */
public class TrainingWeightAnnotationHelper {
    public final static int LATEST_WEIGHT_INDEX =7;
    public final static int INDEX =6;

    /**
     * Ensure that the model will not train using this training weight. Copy the current training weights to previous training weights.
     * @param paragraph
     */
    public static void updatePreviousTrainingWeight(CoreMap paragraph){
        List<Float>  userWeightList = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
        if (userWeightList == null) {
            //First three float are previously trained weight - Definition, TOC and NONE
            //Second set of three floats are currently trained weight
            userWeightList = new ArrayList();
            for (int i=0; i<LATEST_WEIGHT_INDEX*2; i++) {
                userWeightList.add((float)0);
            }
            paragraph.set(CoreAnnotations.TrainingWeightAnnotationFloat.class, userWeightList);
        }
        userWeightList.set(Category.NONE, userWeightList.get(Category.NONE + LATEST_WEIGHT_INDEX));
        for (Integer categoryId : Category.getCategories()) {
            userWeightList.set(categoryId, userWeightList.get(categoryId + LATEST_WEIGHT_INDEX));
        }

    }


    public static void setTrainingWeight(CoreMap paragraph, int index, float userWeight){
        List<Float>  userWeightList = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
        if (userWeightList == null) {
            //First three float are previously trained weight - Definition, TOC and NONE
            //Second set of three floats are currently trained weight
            userWeightList = new ArrayList();
            for (int i=0; i<LATEST_WEIGHT_INDEX*2; i++) {
                userWeightList.add((float)0);
            }
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
    public static double[][] getParagraphWeight(CoreMap paragraph, RandomVariable paraType, ClassifierProto classifierProto) {
        List<Float>  weightList = paragraph.get(CoreAnnotations.TrainingWeightAnnotationFloat.class);
        ClassifierFactory classifierFactory = new ClassifierFactory();
        double[][] weights = new double[2][classifierProto.getCategoryIds().size()];
        int ObservedCategory = CategoryAnnotationHelper.getObservedCategory(paragraph,classifierProto);
        int ObservedClassIndex = CategoryAnnotationHelper.getObservedClassIndex(paragraph, classifierProto);
        weights[0][ObservedClassIndex] = weightList.get(ObservedCategory);
        weights[1][ObservedClassIndex] = weightList.get(ObservedCategory+ LATEST_WEIGHT_INDEX);
        return weights;
    }
}
