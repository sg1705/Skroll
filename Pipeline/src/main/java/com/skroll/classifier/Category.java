package com.skroll.classifier;

import java.util.Arrays;
import java.util.List;

/**
 * Created by saurabhagarwal on 1/5/15.
 */
public class Category {

    public static final int NONE = 0;
    public static final int DEFINITION = 1;
    public static final int TOC_1 = 2;
    public static final int TOC_2 = 3;
    public static final int TOC_3 = 4;
    public static final int TOC_4 = 5;
    public static final int TOC_5 = 6;
    public static final int TOC_6 = 6;

    public static final String DEFINITION_NAME = "com.skroll.classifier.DefinitionCategory";
    public static final String TOC_1_NAME ="com.skroll.classifier.TOC_1";
    public static final String TOC_2_NAME = "com.skroll.classifier.TOC_2";
    public static final String TOC_3_NAME = "com.skroll.classifier.TOC_3";
    public static final String TOC_4_NAME = "com.skroll.classifier.TOC_4";
    public static final String  TOC_5_NAME = "com.skroll.classifier.TOC_5";
    int id;
    String name;

    public static List<Integer> getCategories()  {
        return Arrays.asList(Category.DEFINITION,Category.TOC_1,Category.TOC_2,Category.TOC_3,Category.TOC_4,Category.TOC_5 );
    }


    public Category(int id, String name){
        this.id =id;
        this.name=name;
    }

}


