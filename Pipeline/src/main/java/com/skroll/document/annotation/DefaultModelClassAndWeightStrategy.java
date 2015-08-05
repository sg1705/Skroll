package com.skroll.document.annotation;

import com.skroll.document.CoreMap;
import com.skroll.document.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by saurabhagarwal on 8/3/15.
 */
public class DefaultModelClassAndWeightStrategy implements ModelClassAndWeightStrategy {
    public static final Logger logger = LoggerFactory
            .getLogger(DefaultModelClassAndWeightStrategy.class);

    ManagedCategoryStrategy managedCategoryStrategy;
    UnManagedCategoryStrategy unManagedCategoryStrategy;

    public DefaultModelClassAndWeightStrategy(ManagedCategoryStrategy managedCategoryStrategy, UnManagedCategoryStrategy unManagedCategoryStrategy){
        this.managedCategoryStrategy = managedCategoryStrategy;
        this.unManagedCategoryStrategy = unManagedCategoryStrategy;
    }

    private boolean isCategoryManagedCategory(CoreMap paragraph, List<Integer> categoryIds){
        HashMap<Integer, CoreMap> categoryAnnotation = paragraph.get(CoreAnnotations.CategoryAnnotations.class);
        if (categoryAnnotation == null) return false;
        for (int categoryId : categoryIds) {
            if (categoryAnnotation.containsKey(categoryId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * For a given paragraph, get the index in the list of categoryIds. This index will be used by Model as classIndex.
     * It will return -1 if there in no category annotation
     * @param paragraph
     * @param categoryIds
     * @return Index in list
     */
    @Override
    public int getClassIndexForModel(CoreMap paragraph, List<Integer> categoryIds) {
        if(isCategoryManagedCategory(paragraph,categoryIds)) {
            // handle by managedCategoryStrategy;
            int  classIndexForModel =  managedCategoryStrategy.getClassIndexForModel(paragraph, categoryIds);
            logger.trace("Managed ClassIndexForModel: {}", classIndexForModel);
            return classIndexForModel;

        } else {
            // handle by unmanaged Category
            int  classIndexForModel = unManagedCategoryStrategy.getClassIndexForModel(paragraph, categoryIds);
            logger.debug("UnManaged ClassIndexForModel: {}", classIndexForModel);
            return classIndexForModel;
        }
    }

    /**
     * For a given paragraph, get the categoryId. It must be in the given list of categoryIds.
     * @param paragraph
     * @param categoryIds
     * @return
     */
    @Override
    public int getCategoryIdForModel(CoreMap paragraph, List<Integer> categoryIds) {
        if (isCategoryManagedCategory(paragraph, categoryIds)) {
            // handle by managedCategoryStrategy;
            int categoryIdForModel =  managedCategoryStrategy.getCategoryIdForModel(paragraph, categoryIds);
            logger.trace("Managed CategoryIdForModel: {}", categoryIdForModel);
            return categoryIdForModel;
        } else {
            // handle by unmanaged Category
            int categoryIdForModel =   unManagedCategoryStrategy.getCategoryIdForModel(paragraph, categoryIds);
            logger.debug("UnManaged CategoryIdForModel:{}", categoryIdForModel);
            return categoryIdForModel;
        }
    }

    /**
     * Return only observed paragraph that annotated with one of the category defined in categoryIds list.
     * @param paras
     * @return
     */
    @Override
    public List<CoreMap> getObservedParagraphs(List<CoreMap> paras) {
        List<CoreMap> observedParagraphs = new ArrayList<>();
        for (CoreMap paragraph : paras) {
            if (DocumentHelper.isObserved(paragraph)) {
                        observedParagraphs.add(paragraph);
            }
        }
        return observedParagraphs;
    }


    /**
     * Populate training weight of model using the training weights of categories of a paragraph into array of double that will feed to model.
     * @param paragraph
     * @param categoryIds
     * @return trainingWeights
     */
    @Override
    public double[][] populateTrainingWeights(CoreMap paragraph, List<Integer> categoryIds) {

        if(isCategoryManagedCategory(paragraph,categoryIds)) {
            // handle by managedCategoryStrategy;
            double[][] trainingWeights = managedCategoryStrategy.populateTrainingWeight(paragraph, categoryIds);
            if (logger.isDebugEnabled()) {
                for (double[] w1 : trainingWeights) {
                    for (double w2 : w1)
                        logger.debug("Managed Category trainingWeights: {}", w2);
                }
            }
            return trainingWeights;
        } else {
            // handle by unmanaged Category
            double[][] trainingWeights = unManagedCategoryStrategy.populateTrainingWeight(paragraph, categoryIds);
            if (logger.isDebugEnabled()) {
                for (double[] w1 : trainingWeights) {
                    for (double w2 : w1)
                        logger.debug("Unmanaged Category trainingWeights: {}", w2);
                }
            }
            return trainingWeights;
        }
    }




}
