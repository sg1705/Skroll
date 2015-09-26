package com.skroll.document.annotation;

import com.skroll.document.CoreMap;

import java.util.List;

/**
 * Created by saurabhagarwal on 8/3/15.
 */
public interface ModelClassAndWeightStrategy {

    public int getClassIndexForModel(CoreMap paragraph, List<Integer> categoryIds);

    /**
     * For a given paragraph, get the categoryId. It must be in the given list of categoryIds.
     *
     * @param paragraph
     * @param categoryIds
     * @return
     */
    public int getCategoryIdForModel(CoreMap paragraph, List<Integer> categoryIds);

    /**
     * Return all observed paragraph.
     * @param paras
     * @return
     */
    public List<CoreMap> getObservedParagraphs(List<CoreMap> paras);

    public boolean isObserved(CoreMap para);

    /**
     * Populate training weight of model using the training weights of categories of a paragraph into array of double that will feed to model.
     * @param paragraph
     * @param categoryIds
     * @return trainingWeights
     */
    public double[][] populateTrainingWeights(CoreMap paragraph, List<Integer> categoryIds);

}