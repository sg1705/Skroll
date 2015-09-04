package com.skroll.analyzer.model.applicationModel;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skroll.analyzer.model.RandomVariable;
import com.skroll.analyzer.model.applicationModel.randomVariables.LowerCaseWordsComputer;
import com.skroll.analyzer.model.applicationModel.randomVariables.RVCreater;
import com.skroll.analyzer.model.bn.config.NBMNConfig;
import com.skroll.document.annotation.*;

import java.util.Arrays;
import java.util.List;

/** DocType Model RV Setting defines the Model RVSetting for Doc Type Classifier. It select the doc level and para level features
 *  that are required to be trained the model and classified the doc
 *  Created by saurabhagarwal on 5/11/15.
 */
public class DocTypeModelRVSetting extends ModelRVSetting {

    static final List<RandomVariable> DEFAULT_WORD_FEATURES = Arrays.asList( );

    static final List<RandomVariable> DEFAULT_PARA_FEATURE_VARS = Arrays.asList( );
    static final List<RandomVariable> DEFAULT_PARA_DOC_FEATURE_VARS = Arrays.asList();

    static final List<RandomVariable> DEFAULT_WORD_VARS = Arrays.asList(
            RVCreater.createWordsRVWithComputer(new LowerCaseWordsComputer(), "lowerCaseWords")
    );

    public DocTypeModelRVSetting(List<Integer> categoryIds) {
        super(
                DEFAULT_WORD_FEATURES,
                DEFAULT_PARA_FEATURE_VARS, DEFAULT_PARA_DOC_FEATURE_VARS,
                DEFAULT_WORD_VARS, categoryIds);
    }

    @JsonCreator
    public DocTypeModelRVSetting(
            @JsonProperty("nbmnConfig") NBMNConfig nbmnConfig,
            @JsonProperty("wordType") RandomVariable wordType,
            @JsonProperty("wordFeatures") List<RandomVariable> wordFeatures,
            @JsonProperty("categoryIds") List<Integer> categoryIds) {
        super(nbmnConfig, wordType, wordFeatures, categoryIds);
    }
    @Override
    protected void initializeStrategies() {
        ManagedCategoryStrategy managedCategoryStrategy = new DefaultManagedCategoryStrategy();
        UnManagedCategoryStrategy unManagedCategoryStrategy = new DefaultUnManagedCategoryStrategy();
        this.modelClassAndWeightStrategy = new DefaultModelClassAndWeightStrategy(managedCategoryStrategy, unManagedCategoryStrategy);
    }
}
