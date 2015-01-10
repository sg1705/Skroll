package com.skroll.classification;

import com.skroll.analyzer.model.hmm.HiddenMarkovModel;
import com.skroll.analyzer.model.nb.BinaryNaiveBayesModel;
import com.skroll.analyzer.model.nb.NaiveBayes;
import com.skroll.pipeline.util.Constants;

public class ClassifierFactory {


    private static SECDocumentClassifier secDocumentClassifier = new SECDocumentClassifier();

    public static Classifier getSECDocumentClassifier() {
        return secDocumentClassifier;
    }

}
