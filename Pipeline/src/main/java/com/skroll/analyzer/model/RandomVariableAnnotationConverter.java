//package com.skroll.analyzer.model;
//
//import com.skroll.document.CoreMap;
//import com.skroll.document.Token;
//import com.skroll.document.annotation.CoreAnnotation;
//import com.skroll.document.annotation.CoreAnnotations;
//
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//
///**
// * When the model is created, create random variables from annotations using annotationToRV() methods
// * using the random variables to create model,
// * construct RV -> annotations map, so observed annotations can be easily obtained later for training and inference
// * <p/>
// * <p/>
// * Created by wei on 4/28/15.
// */
//public class RandomVariableAnnotationConverter {
//    static private Map<RandomVariable, CoreAnnotation> map = new HashMap<>();
//
//    int getValue(RandomVariable rv, CoreMap para){
//
//
//    }
//
//    /**
//     * Creating Boolean RV from annotation
//     *
//     * @param annotation
//     * @return
//     */
//    public static RandomVariable annotationToRV(CoreAnnotation<Boolean> annotation) {
//        RandomVariable rv = new RandomVariable(2);
//        map.put(rv, annotation);
//        return rv;
//    }
//
//    /**
//     * Creating general discrete RV from annotation
//     *
//     * @param annotation
//     * @param numVals
//     * @return
//     */
//    public static RandomVariable annotationToRV(CoreAnnotation<Integer> annotation, int numVals) {
//        RandomVariable rv = new RandomVariable(numVals);
//        map.put(rv, annotation);
//        return rv;
//    }
//
//    /**
//     * Creating word RV
//     *
//     * @param annotation
//     * @return
//     */
//    public static RandomVariable annotationToWordRV(CoreAnnotation<List<Token>> annotation) {
//        RandomVariable rv = new RandomVariable(0);
//        map.put(rv, annotation);
//        return rv;
//    }
//
//
//    public <V> CoreAnnotation<V> getAnnotation(RandomVariable rv) {
//        return map.get((rv));
//    }
//    /**
//     * returns 0 if input parameter false, 1 if true
//     * @param b
//     * @return
//     */
//    static int booleanToInt(Boolean b){
//        if (b==null) return 0;
//        return b ? 1:0;
//    }
//
//    public <V> V getValue(CoreMap para, RandomVariable rv) {
//        return booleanToInt(para.get(map.get(rv)));
//    }
//}
