package com.skroll.document;

/**
 * Created by saurabh on 1/1/16.
 */
public enum DocumentFormat {

    HTML(0),
    PDF(1);

    private final int id;

    DocumentFormat(int id) {
        this.id = id;
    }

    int id() {
        return id;
    }
}
