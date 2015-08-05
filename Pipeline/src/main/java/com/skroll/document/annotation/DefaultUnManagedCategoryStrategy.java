package com.skroll.document.annotation;

import com.skroll.classifier.Category;
import com.skroll.document.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabhagarwal on 8/4/15.
 */
public class DefaultUnManagedCategoryStrategy implements UnManagedCategoryStrategy {

    public static final Logger logger = LoggerFactory
            .getLogger(DefaultUnManagedCategoryStrategy.class);


    /**
     * For unManaged category, this method return the class index is zero which represent as NMC in model.
     * @param paragraph
     * @param categoryIds
     * @return Index in list
     */
    public int getClassIndexForModel(CoreMap paragraph, List<Integer> categoryIds) {
        return 0;
    }

    /**
     * For unManaged category, this method return the category Id zero which represent as NMC in model
     * @param paragraph
     * @param categoryIds
     * @return
     */
    @Override
    public int getCategoryIdForModel(CoreMap paragraph, List<Integer> categoryIds) {
        return 0;
    }

    /**
     * For un-managed category, consider class index zero, use the un-managed category weight for class index zero.
     *
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

        int unManagedCategoryId = 0;
        int unManagedCategoryIndex = 0;
        for (int categoryId : Category.getCategories()) {

            if (categoryAnnotation.containsKey(categoryId)) {
                unManagedCategoryId = categoryId;
                break;
            }
        }

        CoreMap annotationCoreMap = categoryAnnotation.get(unManagedCategoryId);
        if(annotationCoreMap==null) {
            logger.error("No category attached to paragraph Id {}, returning null", paragraph.getId());
            return null;
        }
        double[][] trainingWeights = new double[2][categoryIds.size()];

        trainingWeights[0][unManagedCategoryIndex] = annotationCoreMap.get(CoreAnnotations.PriorCategoryWeightFloat.class);
        trainingWeights[1][unManagedCategoryIndex] = annotationCoreMap.get(CoreAnnotations.CurrentCategoryWeightFloat.class);

        return trainingWeights;
    }
}
