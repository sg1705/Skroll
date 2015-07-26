package com.skroll.classifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by saurabh on 4/16/15.
 * Contain the predefined classifiers initialization information.
 * Would be refactor in future to get these information from database or some other means
 */
public class Classifiers {

    //TOC Classifier
    public static final int TOC_CLASSIFIER_ID = 1;
    public static final List<Integer> TOC_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.TOC_1,Category.TOC_2));
    public static final ClassifierProto tocClassifierProto = new ClassifierProto(TOC_CLASSIFIER_ID,TOC_CATEGORY_IDS);

    //Def Classifier
    public static final int DEF_CLASSIFIER_ID = 2;
    public static final List<Integer> DEF_CATEGORY_IDS =  new ArrayList<>(Arrays.asList(Category.NONE,Category.DEFINITION));
    public static final ClassifierProto defClassifierProto = new ClassifierProto(DEF_CLASSIFIER_ID,DEF_CATEGORY_IDS);

}
