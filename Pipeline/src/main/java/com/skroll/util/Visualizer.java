package com.skroll.util;

import com.skroll.analyzer.model.bn.node.DiscreteNode;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
            if (ii == 0) {
                index = "No";
            } else if (ii == 1) {
                index = "Yes";
            }
            if (Double.isNaN(data[ii]) || (Double.isInfinite(data[ii]))) {
                map.put(index, 0.0);
            } else {
                map.put(index, data[ii]);
            }

        }
        return map;
    }

    public static HashMap<String, String> discreteNodeToMap(DiscreteNode node) {
        HashMap<String, String> map = new HashMap();
        map.put("familyVariables", Arrays.toString(node.getFamilyVariables()));
        map.put("parameters", Arrays.toString(node.getParameters()));
        return map;
    }



    public static HashMap<String, HashMap<String, Double>> nodesToMap(DiscreteNode[] nodes) {
        //((TrainingDiscreteNode)(this.tnbfModel.getDocumentFeatureNodeArray()[0])).getProbabilities()
        HashMap<String, HashMap<String, Double>> map = new HashMap();
        for (int ii = 0; ii < nodes.length; ii++) {
            //map.put(nodes[ii].getVariable().name(), Visualizer.toDoubleArrayToMap(nodes[ii].getParameters()));
            map.put(nodes[ii].getVariable().name(), Visualizer.toDoubleArrayToMapWithoutYesNo((nodes[ii]).getParameters()));
        }
        return map;
    }


    public static HashMap<String, Double> toDoubleArrayToMapWithoutYesNo(double[] data) {
        HashMap<String, Double> map = new HashMap();

        for(int ii = 0; ii < data.length; ii++) {
            String index = ""+ii;
            if (Double.isNaN(data[ii]) || (Double.isInfinite(data[ii]))) {
                map.put(index, 0.0);
            } else {
                map.put(index, data[ii]);
            }

        }
        return map;
    }


}
