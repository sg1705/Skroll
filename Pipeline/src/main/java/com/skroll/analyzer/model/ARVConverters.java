//package com.skroll.analyzer.model;
//
///**
// * Created by wei on 5/9/15.
// */
//public class ARVConverters {
//
//    public static class BooleanARVConverter implements ARVConverter{
//        public RandomVariable getRV(){
//            return new RandomVariable(2);
//        }
//
//        public <V> int getValue(V b){
//            if ( b.equals(Boolean.TRUE)) return 1;
//            return 0;
//        }
//    }
//
//    public static class IntegerARVConverter implements ARVConverter{
//        public RandomVariable getRV(int numVals){
//            return new RandomVariable(numVals);
//        }
//
//        public <V> int getValue(V b){
//            if ( b.equals(Boolean.TRUE)) return 1;
//            return 0;
//        }
//    }
//}
//
//}
