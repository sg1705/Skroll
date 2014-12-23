package com.skroll.analyzer.hmm;

import junit.framework.TestCase;

public class HiddenMarkovModelTest extends TestCase {

    public void testUpdateProbabilities() throws Exception {
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);

        String[] token={"a","b","c"};
        int[] tokenType={0,1,1};
        hmm.updateCounts(token, tokenType);
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());
    }

    public void testUpdateCounts() throws Exception {
        HiddenMarkovModel hmm = new HiddenMarkovModel(3);
        String[] token={"a","b","c"};
        int[] tokenType={0,1,1};
        hmm.updateCounts(token, tokenType);
        System.out.println(hmm.showCounts());

    }

    public void testInferForward() throws Exception {

    }

    public void testInferNextStateProbabilities() throws Exception {

    }

    public void testInferJointProbabilitiesStateAndObservation() throws Exception {

    }

    public void testInferStateProbabilitiesGivenObservation() throws Exception {

    }
}