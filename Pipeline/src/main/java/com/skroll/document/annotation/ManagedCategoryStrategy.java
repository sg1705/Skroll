package com.skroll.document.annotation;

import com.skroll.document.CoreMap;

import java.util.List;

/**
 * Created by saurabhagarwal on 8/4/15.
 */
public interface ManagedCategoryStrategy {

    /**
     * For a given paragraph, get the index in the list of categoryIds. This index will be used by Model as classIndex.
     * It will return -1 if there in no category annotation
     * @param paragraph
     * @param categoryIds
     * @return Index in list
     */
    public int getClassIndexForModel(CoreMap paragraph, List<Integer> categoryIds) ;

    /**
     * For a given paragraph, get the categoryId. It must be in the given list of categoryIds.
     * @param paragraph
     * @param categoryIds
     * @return
     */
    public int getCategoryIdForModel(CoreMap paragraph, List<Integer> categoryIds) ;

    /**
     * Populate training weight of model using the training weights of categories of a paragraph into array of double that will feed to model.
     * @param paragraph
     * @param categoryIds
     * @return trainingWeights
     */
    double[][] populateTrainingWeight(CoreMap paragraph, List<Integer> categoryIds);
}
