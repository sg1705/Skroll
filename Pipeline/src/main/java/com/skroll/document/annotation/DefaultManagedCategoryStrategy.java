package com.skroll.document.annotation;

import com.skroll.document.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabhagarwal on 8/4/15.
 */
public class DefaultManagedCategoryStrategy implements ManagedCategoryStrategy {
    public static final Logger logger = LoggerFactory
            .getLogger(DefaultManagedCategoryStrategy.class);

    /**
     * For a given paragraph, get the index in the list of categoryIds. This index will be used by Model as classIndex.
    * It will return -1 if there in no category annotation
    * @param paragraph
    * @param categoryIds
    * @return Index in list
    */
    @Override
    public int getClassIndexForModel(CoreMap paragraph, List<Integer> categoryIds) {

        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null) {
            logger.error("No category attached to paragraph Id {}, returning -1", paragraph.getId());
            return -1;
        }
        for (int index = 0; index < categoryIds.size(); index++) {
            if (categoryAnnotation.containsKey(categoryIds.get(index))) {
                return index;
            }
        }
        return -1;
    }

    /**
     * For a given paragraph, get the categoryId. It must be in the given list of categoryIds.
     * @param paragraph
     * @param categoryIds
     * @return
     */
    @Override
    public int getCategoryIdForModel(CoreMap paragraph, List<Integer> categoryIds) {

        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null) {
            logger.error("No category attached to paragraph Id {}, returning -1", paragraph.getId());
            return -1;
        }
        for (int categoryId : categoryIds) {
            if (categoryAnnotation.containsKey(categoryId)) {
                return categoryId;
            }
        }
        return -1;
    }
    /**
     * Populate training weight of model using the training weights of categories of a paragraph into array of double that will feed to model.
     * @param paragraph
     * @param categoryIds
     * @return trainingWeights
     */
    @Override
    public double[][] populateTrainingWeight(CoreMap paragraph, List<Integer> categoryIds) {

        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation==null) {
            logger.error("No category attached to paragraph Id {}, returning null", paragraph.getId());
            return null;
        }
        int managedCategoryId = -1;
        int managedCategoryIndex = -1;

        for (int index = 0; index < categoryIds.size(); index++) {
            if (categoryAnnotation.containsKey(categoryIds.get(index))) {
                managedCategoryId = categoryIds.get(index);
                managedCategoryIndex = index;
                break;
            }
        }

        CoreMap annotationCoreMap = categoryAnnotation.get(managedCategoryId);

        if(annotationCoreMap==null) return null;

        double[][] trainingWeights = new double[2][categoryIds.size()];

        trainingWeights[0][managedCategoryIndex] = annotationCoreMap.get(CoreAnnotations.PriorCategoryWeightFloat.class);
        trainingWeights[1][managedCategoryIndex] = annotationCoreMap.get(CoreAnnotations.CurrentCategoryWeightFloat.class);

        if (logger.isTraceEnabled()) {
            for (double[] w1 : trainingWeights) {
                for (double w2 : w1)
                    logger.trace("trainingWeights: {}", w2);
            }
        }
        return trainingWeights;
    }
}
