package com.skroll.classification;

public class ClassifierFactory {

    private static SECDocumentClassifier secDocumentClassifier = new SECDocumentClassifier();

    public static Classifier getSECDocumentClassifier() {
        return secDocumentClassifier;
    }

}
