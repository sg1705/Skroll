package com.skroll.analyzer.model.hmm;

import com.skroll.pipeline.SyncPipe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wei2learn on 12/26/2014.
 */
public class HiddenMarkovModelSimpleTestingPipe extends SyncPipe<List<String>, List<Double>> {

    @Override
    public List<Double> process(List<String> input) {
        // fetch the document from config
        HiddenMarkovModel model = (HiddenMarkovModel)config.get(0);
        // fetch the category type
        int category = (Integer)config.get(1);
        List<Double> output = new ArrayList<Double>();

        double[][] hmmTestResult = model.infer(input.toArray(new String[input.size()]));
        //first index is the word

        for(int ii = 0; ii < hmmTestResult.length; ii++) {
            output.add(hmmTestResult[ii][category]);
        }

        return output;
    }
}
