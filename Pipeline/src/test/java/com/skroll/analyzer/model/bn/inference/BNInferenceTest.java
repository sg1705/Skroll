package com.skroll.analyzer.model.bn.inference;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.*;

/**
 * Created by wei on 7/24/15.
 */
public class BNInferenceTest {

    double[] vals = new double[]{1, 2, 3, 4};

    @Test
    public void testNormalizeLog() throws Exception {

        double[] valsCopy = vals.clone();
        BNInference.normalizeLog(valsCopy);
        System.out.println(Arrays.toString(valsCopy));
    }
}