package com.skroll.document;

/**
 * Created by saurabh on 12/29/14.
 */
public enum EntityType {
    DEFINITIONS(1),
    TOC(2);

    private int value;

    private EntityType(int value) {
        this.value = value;
    }
}
