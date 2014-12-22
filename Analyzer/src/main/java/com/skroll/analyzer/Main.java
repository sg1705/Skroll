package com.skroll.analyzer;

import com.skroll.analyzer.nb.BinaryNaiveBayesWithWordsFeatures;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

        //String[] dataFiles = {"data/negativeSamples", "data/positiveSamples"};
        String[] trainingFolder = {"data/negativeFolder", "data/positiveFolder"};
        String testFolder = "data/testFolder";

        String testFile = "data/testData";
        String testFile2 = "data/testData2";

        BufferedReader br=null;
        String line;
        int lineCount=0;
        Map<String,Integer> wordCount = new HashMap<String,Integer>();
        BinaryNaiveBayesWithWordsFeatures NB = new BinaryNaiveBayesWithWordsFeatures();
        //NB.train(0, dataFiles[0]);
        //NB.train(1,dataFiles[1]);
        NB.trainFolder(0, trainingFolder[0]);
        NB.trainFolder(1,trainingFolder[1]);
        //NB.showMap();
        //NB.showInverseMap();
        //NB.showMapSortedByValues();
        NB.showWordsImportance();
        NB.testFolder(testFolder);
        //NB.showImportantWords();

        //System.out.println(NB);
//
        //NB.test(testFile2);
        NB.testWords(testFile);
//        System.out.println(NB.inferJointProbability(1,new String[]{"means"}));
//        System.out.println(NB.inferCategoryProbability(new String[]{"pursuant"}));
//        System.out.println(NB.inferCategoryProbability(new String[]{"sale","pursuant"}));
//        System.out.println(NB.inferCategoryProbability(new String[]{"sale"}));

//        System.out.println(NB.inferCategoryProbability(new String[]{"abc"}));
//        System.out.println(NB.inferCategoryProbabilityMoreStable(new String[]{"abc"}));
        //System.out.println(NB.inferCategoryProbability(new String[]{"by"}));
        //System.out.println(NB.inferCategoryProbabilityMoreStable(new String[]{"by"}));



    }
}
