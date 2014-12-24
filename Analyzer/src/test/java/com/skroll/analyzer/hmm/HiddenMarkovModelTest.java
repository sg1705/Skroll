package com.skroll.analyzer.hmm;

import junit.framework.TestCase;

import java.util.Arrays;

public class HiddenMarkovModelTest extends TestCase {

    public void testUpdateProbabilities() throws Exception {
        HiddenMarkovModel hmm = new HiddenMarkovModel(5);
//        String[] token={"a","b","c"};
        String[] token={"\"","affiliate","\"","means","respect"};
        int[] tokenType={0,1,0,0,0};

        hmm.updateCounts(token, tokenType);
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());
    }

    public void testUpdateCounts() throws Exception {
        HiddenMarkovModel hmm = new HiddenMarkovModel(10);
//        String[] token={"a","b","c"};
        //String[] token={"\"","affiliate","\"","means","respect"};
        String[] token={"\"","Agreement","\"","has", "the", "meaning", "specified", "in", "the", "introductory", "paragraph", "hereof"};
        int[] tokenType={0,1,0,0,0,0,0,0,0,0,0,0};
        hmm.updateCounts(token, tokenType);
        System.out.println(hmm.showCounts());

    }

    public void testInferForward() throws Exception {
        System.out.println("testInferForward");
        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(5);
        String[] token={"\"","affiliate","\"","means","respect"};
        int[] tokenType={0,1,0,0,0};

        hmm.updateCounts(token, tokenType);
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println(Arrays.deepToString(hmm.inferForward(token)) );

    }

    public void testInferNextStateProbabilities() throws Exception {
        System.out.println("testInferNextStateProbabilities");
        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(5);
        String[] token={"\"","affiliate","\"","means","respect"};
        int[] tokenType={0,1,0,0,0};

        hmm.updateCounts(token, tokenType);
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println(Arrays.toString(hmm.inferNextStateProbabilities(prob)) );

    }

    public void testInferJointProbabilitiesStateAndObservation() throws Exception {
        System.out.println("testInferJointProbabilitiesStateAndObservation");

        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(5);
        String[] token={"\"","affiliate","\"","means","respect"};
        int[] tokenType={0,1,0,0,0};

        hmm.updateCounts(token, tokenType);
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println(Arrays.toString(hmm.inferJointProbabilitiesStateAndObservation(2, prob, token)) );
    }

    public void testInferStateProbabilitiesGivenObservation() throws Exception {
        System.out.println("testInferStateProbabilitiesGivenObservation");

        double [] prob = {0.5,0.5};
        HiddenMarkovModel hmm = new HiddenMarkovModel(5);
        String[] token={"\"","affiliate","\"","means","respect"};
        int[] tokenType={0,1,0,0,0};

        hmm.updateCounts(token, tokenType);
        System.out.println(hmm.showProbabilities());

        hmm.updateProbabilities();
        System.out.println(hmm.showProbabilities());

        System.out.println("state prob "+Arrays.toString(prob)+'\n');
        System.out.println(Arrays.toString(hmm.inferStateProbabilitiesGivenObservation(1, prob, token)) );
    }

}