package com.skroll.document;

/**
 * Created by saurabh on 12/29/14.
 */
public enum EntityType {
    DefinedTermsAnnotation(1);

    private int value;

    private EntityType(int value) {
        this.value = value;
    }
}
