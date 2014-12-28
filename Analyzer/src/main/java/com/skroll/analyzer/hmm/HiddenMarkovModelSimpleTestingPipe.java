package com.skroll.analyzer.hmm;

import com.google.common.primitives.Doubles;
import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei2learn on 12/26/2014.
 */
public class HiddenMarkovModelSimpleTestingPipe extends SyncPipe<List<String>, List<Double>> {

    @Override
    public List<Double> process(List<String> input) {
        // fetch the model from config
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);
        // fetch the category type
        int category = (Integer)config.get(1);
        List<Double> output = new ArrayList<Double>();

        double[][] hmmTestResult = model.infer(input.toArray(new String[input.size()]));
        output = Doubles.asList(hmmTestResult[category]);

        return output;
    }
}
