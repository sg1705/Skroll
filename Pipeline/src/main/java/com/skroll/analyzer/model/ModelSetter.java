package com.skroll.analyzer.model;

import com.skroll.document.annotation.CoreAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Set up the model, get the model config
 * Created by wei on 5/10/15.
 */
public class ModelSetter {

    RandomVariable paraStartsWithQuote = RandomVariableCreater.createParagraphStartsWithRV(
            CoreAnnotations.InQuotesAnnotation.class);
    List<RandomVariable> paraDocFeatures = Arrays.asList(paraStartsWithQuote);
    List<RandomVariable> docFeatures = Arrays.asList(new RandomVariable(2, "docQuotes"));
}
