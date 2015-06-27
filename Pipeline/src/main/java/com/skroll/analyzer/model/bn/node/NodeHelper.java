package com.skroll.analyzer.model.bn.node;

import com.skroll.analyzer.model.RandomVariable;

/**
 * Created by wei on 6/13/15.
 */
public class NodeHelper {
    // calculation of the size up to (not include) the variable at the specified index.
    static int sizeUpTo(DiscreteNode node, int index) {
        RandomVariable[] familyVariables = node.getFamilyVariables();
        int size = 1;
        for (int i = 0; i < index; i++)
            size *= familyVariables[i].getFeatureSize();
        return size;
    }
}
