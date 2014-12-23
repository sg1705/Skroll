package com.skroll.model;

import java.util.List;

/**
 * Created by saurabh on 12/22/14.
 */
public class Paragraph {

    private String id;
    private String text;
    private List<String> words;

    public Paragraph(String id, String text) {
        this.id = id;
        this.text = text;
    }


}
