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
    public static final int USER_TOC = 4;
    public static final int TOC_3 = 5;

    //Document Type
    public static final int DOCTYPE_NONE = 100;
    public static final int TEN_K = 101;
    public static final int TEN_Q = 102;
    public static final int INDENTURE = 103;

    int id;
    String name;

    public static List<Integer> getCategories()  {
        return Arrays.asList(Category.NONE, Category.DEFINITION,Category.TOC_1,Category.TOC_2, Category.USER_TOC); //Category.TOC_3,Category.TOC_4,Category.TOC_5 );
    }
    public static List<Integer> getCategoriesExcludingNONE()  {
        return Arrays.asList(Category.DEFINITION,Category.TOC_1,Category.TOC_2, Category.USER_TOC); //Category.TOC_3,Category.TOC_4,Category.TOC_5 );
    }

    public Category(int id, String name){
        this.id =id;
        this.name=name;
    }

    public static List<Integer> getDocType()  {
        return Arrays.asList(TEN_K,TEN_Q,INDENTURE);
    }

}


