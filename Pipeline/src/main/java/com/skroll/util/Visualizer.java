package com.skroll.util;

import java.util.HashMap;

/**
 * Visual to create json for all things
 * related to the BNI model.
 *
 * Need to visualize an array of double
 * Created by saurabh on 3/29/15.
 */
public class Visualizer {

    public static HashMap<String, Double> toDoubleArrayToMap(double[] data) {
        HashMap<String, Double> map = new HashMap();

        for(int ii = 0; ii < data.length; ii++) {
            String index = ""+ii;
            map.put(index, data[ii]);
        }
        return map;
    }

}
